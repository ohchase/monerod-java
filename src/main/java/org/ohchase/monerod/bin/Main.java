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

        RestrictedRpcConfig restrictedRpcConfig = RestrictedRpcConfig.builder()
                .restrictedIp("127.0.0.1")
                .restrictedPort(38081)
                .build();

        RpcConfig rpcConfig = RpcConfig.builder()
                .unrestrictedIp("127.0.0.1")
                .unrestrictedPort(38082)
                .build();

        DaemonConfig daemonConfig = DaemonConfig.builder()
                .networkType(NetworkType.STAGE_NET)
                .dataDirectory(dataDirectory)
                .p2PConfig(p2PConfig)
                .restrictedRpcConfig(restrictedRpcConfig)
                .rpcConfig(rpcConfig)
                .build();

        IDaemonListener listener = new IDaemonListener() {
            @Override
            public void onRpcReady() {
                System.out.println("RPC is ready.");
            }

            @Override
            public void onP2PReady() {
                System.out.println("P2P is ready.");
            }

            @Override
            public void onDaemonStarted() {
                System.out.println("Daemon is started.");
            }

            @Override
            public void onNewTopBlockCandidate(long currentHeight, long candidateHeight) {
                System.out.println("New top block candidate: " + candidateHeight + " (current: " + currentHeight + ")");
            }

            @Override
            public void onSyncProgress(long currentHeight, long targetHeight) {
                System.out.println("Sync progress: " + currentHeight + " / " + targetHeight);
            }

        };

        System.out.println("Daemon Settings:");
        System.out.println("  Network Type: " + daemonConfig.getNetworkType());
        System.out.println("  Data Directory: " + daemonConfig.getDataDirectory().toAbsolutePath());

        DaemonProcess.start(monerodBinary, listener, daemonConfig);
        while (true) {
            Thread.sleep(Duration.ofMinutes(1).toMillis());
        }

    }
}