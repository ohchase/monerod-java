package org.ohchase.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ohchase.core.configuration.DaemonConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class DaemonProcess {

    /**
     * Pattern to match new top block candidate lines like:
     * Sync data returned a new top block candidate: 371876 -> 2020778
     */
    private static final Pattern NEW_TOP_BLOCK_CANDIDATE_PATTERN = Pattern.compile("Sync data returned a new top block candidate: (\\d+) -> (\\d+)");

    /**
     * Pattern to match sync progress lines like:
     * Synced 372056/2020778 (18%, 1648722 left)
     */
    private static final Pattern SYNC_PROGRESS_PATTERN = Pattern.compile("Synced (\\d+)/(\\d+) \\((\\d+)%, (\\d+) left\\)");

    @Getter
    private final File monerodBinary;

    @Getter
    private final DaemonConfig daemonConfig;

    @Getter
    private final IDaemonListener daemonListener;

    @Getter
    private final Process process;

    @Getter
    private final Thread listenerThread;

    public static DaemonProcess start(File monerodBinary, IDaemonListener daemonListener, DaemonConfig daemonConfig) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> command = buildCommand(monerodBinary, daemonConfig);

        processBuilder.command(command);
        processBuilder.environment().put("LANG", "en_US.UTF-8"); // scrape output in English
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // read process output until success
        String initializationLine;
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        boolean success = false;

        while ((initializationLine = in.readLine()) != null) {
            sb.append(initializationLine).append("\n");

            // notify listener of p2p ready
            if (initializationLine.contains("p2p server initialized OK")) {
                daemonListener.onP2PReady();
            }

            // notify listener of rpc ready
            if (initializationLine.contains("core RPC server started ok")) {
                daemonListener.onRpcReady();
            }

            // read success message
            if (initializationLine.contains("Starting p2p net loop")) {
                daemonListener.onDaemonStarted();
                success = true;
                break;
            }
        }

        // continue printing output in separate, non-blocking thread
        Thread listenerThread = createListenerThread(daemonListener, process);

        if (!success) {
            process.destroy();
            throw new IOException("Failed to start monerod process. Output:\n" + sb);
        }

        return new DaemonProcess(monerodBinary, daemonConfig, daemonListener, process, listenerThread);
    }

    private static List<String> buildCommand(File monerodBinary, DaemonConfig daemonConfig) {
        List<String> command = new ArrayList<>();
        command.add(monerodBinary.getAbsolutePath());

        switch (daemonConfig.getNetworkType()) {
            case MAIN_NET -> {
                // no additional flags needed
            }
            case STAGE_NET -> command.add("--stagenet");
            case TEST_NET -> command.add("--testnet");
        }

        command.add("--data-dir");
        command.add(daemonConfig.getDataDirectory().toAbsolutePath().toString());

        // Opinionated configuration
        command.add("--no-zmq");

        if (daemonConfig.getP2PConfig() != null) {
            command.add("--no-igd");

            command.add("--p2p-bind-ip");
            command.add(daemonConfig.getP2PConfig().getP2pIp());

            command.add("--p2p-bind-port");
            command.add(String.valueOf(daemonConfig.getP2PConfig().getP2pPort()));
        }

        if (daemonConfig.getRpcConfig() != null) {
            command.add("--rpc-bind-ip");
            command.add(daemonConfig.getRpcConfig().getUnrestrictedIp());

            command.add("--rpc-bind-port");
            command.add(String.valueOf(daemonConfig.getRpcConfig().getUnrestrictedPort()));
        }


        return command;
    }

    private static Thread createListenerThread(IDaemonListener daemonListener, Process process) {
        Thread listenerThread = new Thread(() -> {
            try {
                String stdoutLine;
                BufferedReader stdoutBuffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((stdoutLine = stdoutBuffer.readLine()) != null) {
                    if (stdoutLine.contains("Synced")) {
                        Matcher matcher = SYNC_PROGRESS_PATTERN.matcher(stdoutLine);
                        if (matcher.find()) {
                            long currentHeight = Long.parseLong(matcher.group(1));
                            long targetHeight = Long.parseLong(matcher.group(2));
                            daemonListener.onSyncProgress(currentHeight, targetHeight);
                        }
                    }

                    if (stdoutLine.contains("Sync data returned a new top block candidate")) {
                        Matcher matcher = NEW_TOP_BLOCK_CANDIDATE_PATTERN.matcher(stdoutLine);
                        if (matcher.find()) {
                            long currentHeight = Long.parseLong(matcher.group(1));
                            long candidateHeight = Long.parseLong(matcher.group(2));
                            daemonListener.onNewTopBlockCandidate(currentHeight, candidateHeight);
                        }
                    }
                }
            } catch (IOException e) {
                // e.printStackTrace(); // exception expected on close
            }
        });
        listenerThread.start();
        return listenerThread;
    }

}
