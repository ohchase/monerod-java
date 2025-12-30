package org.ohchase.monerod.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Configuration for the inbound proxy.
 */
@Builder
public class InboundProxy {

    @Getter
    @NonNull
    private final String hsAddress;

    @Getter
    @NonNull
    private final Integer hsPort;

    @Getter
    @NonNull
    private final String forwardingAddress;

    @Getter
    @NonNull
    private final Integer forwardingPort;

    @Getter
    @NonNull
    private final Integer maxConnections;

}
