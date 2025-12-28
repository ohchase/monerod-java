package org.ohchase.monerod.bin;

import org.ohchase.monerod.DaemonProcess;
import org.ohchase.monerod.IDaemonListener;
import org.ohchase.monerod.NetworkType;
import org.ohchase.monerod.configuration.DaemonConfig;
import org.ohchase.monerod.configuration.P2PConfig;
import org.ohchase.monerod.configuration.RestrictedRpcConfig;
import org.ohchase.monerod.configuration.RpcConfig;

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
                .p2pIp("0.0.0.0")
                .p2pPort(38080)
                .build();

        RpcConfig rpcConfig = RpcConfig.builder()
                .unrestrictedIp("127.0.0.1")
                .unrestrictedPort(38081)
                .build();

        RestrictedRpcConfig restrictedRpcConfig = RestrictedRpcConfig.builder()
                .restrictedIp("127.0.0.1")
                .restrictedPort(8080)
                .build();

        DaemonConfig daemonConfig = DaemonConfig.builder()
                .networkType(NetworkType.STAGE_NET)
                .dataDirectory(dataDirectory)
                .p2PConfig(p2PConfig)
                .restrictedRpcConfig(restrictedRpcConfig)
                .rpcConfig(rpcConfig)
                .getSyncPrunedBlocks(true)
                .getPrunedBlockchain(true)
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
            System.out.println("Daemon is running...");
            System.out.println("  Uptime: " + daemonProcess.getUptime().toSeconds() + " seconds");
            Thread.sleep(Duration.ofSeconds(10).toMillis());

            daemonProcess.stop();
        }

        System.out.println("Daemon process has exited.");
        System.out.println("  Exit Code: " + daemonProcess.getExitCode());
    }
}