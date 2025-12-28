package org.ohchase.monerod.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.nio.file.Path;

/**
 * Configuration for the Daemon process.
 * This includes network type, data directory, and configurations for P2P and RPC interfaces.
 */
@Builder
public class DaemonConfig {

    /**
     * Type of network the daemon will operate on (e.g., MAIN_NET, TEST_NET, STAGE_NET).
     */
    @Getter
    @NonNull
    private final NetworkType networkType;

    /**
     * Path to the data directory where the daemon will store its data.
     */
    @Getter
    @NonNull
    private final Path dataDirectory;

    /**
     * Configuration for the P2P interface.
     */
    @Getter
    private final P2PConfig p2PConfig;

    /**
     * Configuration for the restricted RPC interface.
     * <b>USE WITH CAUTION AND INTENTION.</b>
     * <b>If this is exposed to untrusted networks, it may pose security risks.</b>
     * <b>If this is exposed to untrusted networks, it will allow external entities to begin mining.</b>
     * <b>If this is exposed to untrusted networks, it will allow external entities to drain loaded wallets.</b>
     */
    @Getter
    private final RestrictedRpcConfig restrictedRpcConfig;

    /**
     * Configuration for the unrestricted RPC interface.
     */
    @Getter
    private final RpcConfig rpcConfig;

    /**
     * Whether to run the daemon in pruned mode.
     */
    @Getter
    private final Boolean getSyncPrunedBlocks;

    /**
     * Whether to accept pruned blocks from peers.
     */
    @Getter
    private final Boolean getPrunedBlockchain;

}
