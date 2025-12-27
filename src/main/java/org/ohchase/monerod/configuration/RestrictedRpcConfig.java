package org.ohchase.monerod.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Configuration for restricted RPC interface.
 * This interface has limited access for security reasons.
 * It is typically used to provide public information about the blockchain without exposing sensitive operations.
 */
@Builder
public class RestrictedRpcConfig {

    /**
     * IP address for the restricted RPC interface.
     */
    @Getter
    @NonNull
    private final String restrictedIp;

    /**
     * Port number for the restricted RPC interface.
     */
    @Getter
    @NonNull
    private final Integer restrictedPort;
}
