package org.ohchase.core.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.ohchase.core.NetworkType;

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
     */
    @Getter
    private final RestrictedRpcConfig restrictedRpcConfig;

    /**
     * Configuration for the unrestricted RPC interface.
     */
    @Getter
    private final RpcConfig rpcConfig;

}
