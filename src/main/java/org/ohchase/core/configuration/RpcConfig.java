package org.ohchase.core.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
public class RpcConfig {

    @Getter
    private @NonNull final String unrestrictedIp;

    @Getter
    private @NonNull final int unrestrictedPort;
}
