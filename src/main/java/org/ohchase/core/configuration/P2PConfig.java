package org.ohchase.core.configuration;

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
    private final String p2pIp;

    /**
     * Port number for the P2P interface.
     */
    @Getter
    @NonNull
    private final Integer p2pPort;

}
