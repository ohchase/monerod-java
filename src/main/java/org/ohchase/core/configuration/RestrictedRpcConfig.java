package org.ohchase.core.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
public class RestrictedRpcConfig {

    @Getter
    private @NonNull final String restrictedIp;

    @Getter
    private @NonNull final int restrictedPort;
}
