package org.ohchase.core.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.ohchase.core.NetworkType;

import java.nio.file.Path;

@Builder
public class DaemonConfig {

    @Getter
    private final @NonNull NetworkType networkType;

    @Getter
    private final @NonNull Path dataDirectory;

    @Getter
    private final P2PConfig p2PConfig;

    @Getter
    private final RestrictedRpcConfig restrictedRpcConfig;

    @Getter
    private final RpcConfig rpcConfig;

}
