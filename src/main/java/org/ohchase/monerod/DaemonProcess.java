package org.ohchase.monerod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ohchase.monerod.configuration.DaemonConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a running Daemon process.
 * Manages the lifecycle and output of the daemon.
 */
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
    private final Path monerodBinary;

    @Getter
    private final DaemonConfig daemonConfig;

    @Getter
    private final IDaemonListener daemonListener;

    private final Process process;

    private final Thread listenerThread;

    /**
     * Stops the daemon process and waits for the listener thread to finish.
     */
    public int stop() {
        process.destroy();

        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            System.out.println("Warning. Interrupted while waiting for listener thread to finish.");
            Thread.currentThread().interrupt();
        }

        try {
            int exitCode = process.waitFor();
            this.daemonListener.onDaemonExited(exitCode);
            return exitCode;
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for process to terminate.");
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    /**
     * Gets the uptime of the daemon process.
     * @return Duration representing the uptime.
     * @throws NoSuchElementException if the start instant is not available.
     */
    public Duration getUptime() throws NoSuchElementException {
        long uptimeMillis = System.currentTimeMillis() - process.info().startInstant().orElseThrow().toEpochMilli();
        return Duration.ofMillis(uptimeMillis);
    }

    /**
     * Checks if the daemon process is still running.
     * @return true if running, false if exited.
     */
    public boolean isAlive() {
        return process.isAlive();
    }

    /**
     * Gets the exit code of the daemon process.
     * @return exit code.
     * @throws IllegalThreadStateException if the process is still running.
     */
    public int getExitCode() throws IllegalThreadStateException {
        return process.exitValue();
    }

    /**
     * Starts the daemon process with the given configuration and listener.
     * @param monerodBinary Path for the monerod binary.
     * @param daemonListener Listener for daemon events.
     * @param daemonConfig Configuration for the daemon.
     * @return DaemonProcess on successful start.
     * @throws IOException if the process fails to start.
     */
    public static DaemonProcess start(Path monerodBinary, IDaemonListener daemonListener, DaemonConfig daemonConfig) throws IOException {
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
        if (!success) {
            process.destroy();
            throw new IOException("Failed to start monerod process. Output:\n" + sb);
        }

        // continue printing output in separate, non-blocking thread, and notify of events.
        Thread listenerThread = createListenerThread(daemonListener, in);
        return new DaemonProcess(monerodBinary, daemonConfig, daemonListener, process, listenerThread);
    }

    private static List<String> buildCommand(Path monerodBinary, DaemonConfig daemonConfig) {
        List<String> command = new ArrayList<>();
        command.add(monerodBinary.toAbsolutePath().toString());

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

        // Pruning configuration
        if (daemonConfig.getSyncPrunedBlocks() != null
                && daemonConfig.getSyncPrunedBlocks()) {
            command.add("--sync-pruned-blocks");
        }

        // Accept pruned blocks from peers
        if (daemonConfig.getPrunedBlockchain() != null
                && daemonConfig.getPrunedBlockchain()) {
            command.add("--prune-blockchain");
        }

        // P2P Configuration
        if (daemonConfig.getP2PConfig() != null) {
            command.add("--no-igd");

            command.add("--p2p-bind-ip");
            command.add(daemonConfig.getP2PConfig().getP2pIp());

            command.add("--p2p-bind-port");
            command.add(String.valueOf(daemonConfig.getP2PConfig().getP2pPort()));
        }

        // Unrestricted RPC Configuration
        if (daemonConfig.getRpcConfig() != null) {
            command.add("--rpc-bind-ip");
            command.add(daemonConfig.getRpcConfig().getUnrestrictedIp());

            command.add("--rpc-bind-port");
            command.add(String.valueOf(daemonConfig.getRpcConfig().getUnrestrictedPort()));
        }

        // Restricted RPC Configuration
        if (daemonConfig.getRestrictedRpcConfig() != null) {
            command.add("--rpc-restricted-bind-ip");
            command.add(daemonConfig.getRestrictedRpcConfig().getRestrictedIp());

            command.add("--rpc-restricted-bind-port");
            command.add(String.valueOf(daemonConfig.getRestrictedRpcConfig().getRestrictedPort()));
        }

        // Transaction proxy configuration
        // Send transactions over Tor or I2P
        if (daemonConfig.getTxProxy() != null) {
            StringBuilder txProxySb = new StringBuilder();

            switch (daemonConfig.getTxProxy().getType()) {
                case TOR -> txProxySb.append("tor");
                case I2P -> txProxySb.append("i2p");
            }

            txProxySb.append(",");
            txProxySb.append(daemonConfig.getTxProxy().getAddress());
            txProxySb.append(":");
            txProxySb.append(daemonConfig.getTxProxy().getPort());

            if (daemonConfig.getTxProxy().getMaxConnections() != null) {
                txProxySb.append(",");
                txProxySb.append(daemonConfig.getTxProxy().getMaxConnections());
            }

            if (daemonConfig.getTxProxy().isDisableNoise()) {
                txProxySb.append(",disable_noise");
            }

            String txProxyCommand = txProxySb.toString();
            command.add("--tx-proxy");
            command.add(txProxyCommand);
        }


        return command;
    }

    private static Thread createListenerThread(IDaemonListener daemonListener, BufferedReader stdoutBuffer) {
        Thread stdoutThread = new Thread(() -> {
            try {
                String stdoutLine;
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
                daemonListener.onDaemonKilled();
            } catch (IOException e) {
                // Stream closed, exit thread
            }
        });
        stdoutThread.start();
        return stdoutThread;
    }
}
