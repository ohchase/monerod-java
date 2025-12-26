package org.ohchase;

import org.ohchase.core.DaemonProcess;
import org.ohchase.core.IDaemonListener;
import org.ohchase.core.configuration.DaemonConfig;
import org.ohchase.core.NetworkType;
import org.ohchase.core.configuration.P2PConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        File monerodBinary = new File(args[0]);
        Path dataDirectory = Path.of(args[1]);

        System.out.println("Monerod Binary: " + monerodBinary.getAbsolutePath());
        System.out.println("Data Directory: " + dataDirectory.toAbsolutePath());

        P2PConfig p2PConfig = P2PConfig.builder()
                .p2pIp("0.0.0.0")
                .p2pPort(63303)
                .build();

        DaemonConfig daemonConfig = DaemonConfig.builder()
                .networkType(NetworkType.STAGE_NET)
                .dataDirectory(dataDirectory)
                .p2PConfig(p2PConfig)
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

        DaemonProcess daemonProcess = DaemonProcess.start(monerodBinary, listener, daemonConfig);
        while (true) {
            Thread.sleep(Duration.ofMinutes(1).toMillis());
        }

    }
}