package org.ohchase.monerod.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Configuration for unrestricted RPC interface.
 * This interface provides full access to the daemon's functionalities.
 * It should be secured and not exposed to untrusted networks.
 */
@Builder
public class RpcConfig {

    /**
     * IP address for the unrestricted RPC interface.
     */
    @Getter
    @NonNull
    private final String unrestrictedIp;

    /**
     * Port number for the unrestricted RPC interface.
     */
    @Getter
    @NonNull
    private final Integer unrestrictedPort;
}
