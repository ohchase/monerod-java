package org.ohchase.monerod.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Configuration for the transaction proxy.
 */
@Builder
public class TxProxy {

    /**
     * Type of transaction proxy (e.g., TOR, I2P).
     */
    @Getter
    @NonNull
    private final TxProxyType type;

    /**
     * Address of the transaction proxy.
     */
    @Getter
    @NonNull
    private final String address;

    /**
     * Port of the transaction proxy.
     */
    @Getter
    @NonNull
    private final Integer port;

    /**
     * Maximum number of connections to the transaction proxy.
     */
    @Getter
    private final Integer maxConnections;

    /**
     * Whether to disable noise for the transaction.
     * If disable noise is enabled the tx is "fluffed" to outbound Onion and I2P peers.
     * The receiving hidden service will immediately fluff the transaction to ipv4/6 peers.
     * This will speed up tx broadcast.
     */
    @Getter
    private final boolean disableNoise;

}
