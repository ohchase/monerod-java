package org.ohchase.monerod.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Configuration for a network proxy.
 */
@Builder
public class ProxyConfig {

    /**
     * IP address of the proxy.
     */
    @Getter
    @NonNull
    private final String address;

    /**
     * Port number of the proxy.
     */
    @Getter
    @NonNull
    private final Integer port;
}
