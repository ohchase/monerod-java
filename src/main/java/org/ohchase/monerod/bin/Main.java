package org.ohchase.monerod.bin;

import org.ohchase.monerod.DaemonProcess;
import org.ohchase.monerod.IDaemonListener;
import org.ohchase.monerod.configuration.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Path monerodBinary = Path.of(args[0]);
        Path dataDirectory = Path.of(args[1]);

        System.out.println("Monerod Binary: " + monerodBinary.toAbsolutePath());
        System.out.println("Data Directory: " + dataDirectory.toAbsolutePath());

        P2PConfig p2PConfig = P2PConfig.builder()
                .address("127.0.0.1")
                .port(38080)
                .proxy(ProxyConfig.builder()
                        .address("127.0.0.1")
                        .port(9050)
                        .build()
                )
                .build();

        RpcConfig rpcConfig = RpcConfig.builder()
                .address("127.0.0.1")
                .port(38081)
                .build();

        RestrictedRpcConfig restrictedRpcConfig = RestrictedRpcConfig.builder()
                .address("127.0.0.1")
                .port(8080)
                .build();

        TxProxy txProxy = TxProxy.builder()
                .type(TxProxyType.TOR)
                .address("127.0.0.1")
                .port(9050)
                .maxConnections(16)
                .disableNoise(true)
                .build();

        DaemonConfig daemonConfig = DaemonConfig.builder()
                .networkType(NetworkType.STAGE_NET)
                .dataDirectory(dataDirectory)
                .p2pConfig(p2PConfig)
                .restrictedRpcConfig(restrictedRpcConfig)
                .rpcConfig(rpcConfig)
                .txProxy(txProxy)
                .syncPrunedBlocks(true)
                .prunedBlockchain(true)
                .build();

        IDaemonListener listener = new IDaemonListener() {
            @Override
            public void onRpcReady() {
                System.out.println("[LISTENER] RPC is ready.");
            }

            @Override
            public void onP2PReady() {
                System.out.println("[LISTENER] P2P is ready.");
            }

            @Override
            public void onDaemonStarted() {
                System.out.println("[LISTENER] Daemon is started.");
            }

            @Override
            public void onDaemonExited(int exitCode) {
                System.out.println("[LISTENER] Daemon has exited with code: " + exitCode);
            }

            @Override
            public void onDaemonKilled() {
                System.out.println("[LISTENER] Daemon has been killed externally.");
            }

            @Override
            public void onNewTopBlockCandidate(long currentHeight, long candidateHeight) {
                System.out.println("[LISTENER] New top block candidate: " + candidateHeight + " (current: " + currentHeight + ")");
            }

            @Override
            public void onSyncProgress(long currentHeight, long targetHeight) {
                System.out.println("[LISTENER] Sync progress: " + currentHeight + " / " + targetHeight);
            }

        };

        System.out.println("Daemon Settings:");
        System.out.println("  Network Type: " + daemonConfig.getNetworkType());
        System.out.println("  Data Directory: " + daemonConfig.getDataDirectory().toAbsolutePath());

        DaemonProcess daemonProcess = DaemonProcess.start(monerodBinary, listener, daemonConfig);
        while (daemonProcess.isAlive()) {
            System.out.println("Daemon is running... Uptime: " + daemonProcess.getUptime().toSeconds() + " seconds");
            Thread.sleep(Duration.ofSeconds(10).toMillis());

            // For demonstration purposes, we will not stop the daemon automatically.
            // daemonProcess.stop();
        }

        System.out.println("Daemon process has exited.");
        System.out.println("  Exit Code: " + daemonProcess.getExitCode());
    }
}