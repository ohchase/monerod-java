package org.ohchase.monerod;

import java.io.IOException;

/**
 * Listener interface for Daemon events.
 * Implement this interface to receive notifications about the daemon's state and progress.
 */
public interface IDaemonListener {

    /**
     * Called when the RPC interface is ready.
     */
    void onRpcReady();

    /**
     * Called when the P2P interface is ready.
     */
    void onP2PReady();

    /**
     * Called when the daemon has started.
     * This represents the point at which the daemon process is up and running.
     * This does not necessarily mean that the daemon is fully synchronized with the network.
     * This is a good point to start monitoring synchronization progress.
     */
    void onDaemonStarted();

    /**
     * Called when the daemon is killed by an external mechanism.
     */
    void onDaemonKilled();

    /**
     * Called once the daemon process has exited.
     * @param exitCode the exit code of the daemon process
     */
    void onDaemonExited(int exitCode);

    /**
     * Called when a new top block candidate is found.
     * @param currentHeight current blockchain height
     * @param candidateHeight new candidate blockchain height
     */
    void onNewTopBlockCandidate(long currentHeight, long candidateHeight);

    /**
     * Called to report synchronization progress.
     * @param currentHeight current blockchain height
     * @param targetHeight target blockchain height
     */
    void onSyncProgress(long currentHeight, long targetHeight);

}
