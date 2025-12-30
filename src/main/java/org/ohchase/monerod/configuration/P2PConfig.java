package org.ohchase.monerod.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Configuration for the P2P interface.
 * This interface is used for peer-to-peer communication between nodes in the network.
 */
@Builder
public class P2PConfig {

    /**
     * IP address for the P2P interface.
     */
    @Getter
    @NonNull
    private final String address;

    /**
     * Port number for the P2P interface.
     */
    @Getter
    @NonNull
    private final Integer port;

    /**
     * Configuration for the proxy used in P2P communication.
     */
    @Getter
    private final ProxyConfig proxy;

    /**
     * Configuration for Hidden Service Inbound Proxy.
     */
    @Getter
    private final InboundProxy inboundProxy;

}
