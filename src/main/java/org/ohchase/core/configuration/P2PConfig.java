package org.ohchase.core.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
public class P2PConfig {

    @Getter
    private @NonNull final String p2pIp;

    @Getter
    private @NonNull final int p2pPort;

}
