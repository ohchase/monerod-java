package org.ohchase.core;

public interface IDaemonListener {

    void onRpcReady();

    void onP2PReady();

    void onDaemonStarted();

    void onNewTopBlockCandidate(long currentHeight, long candidateHeight);

    void onSyncProgress(long currentHeight, long targetHeight);

}
