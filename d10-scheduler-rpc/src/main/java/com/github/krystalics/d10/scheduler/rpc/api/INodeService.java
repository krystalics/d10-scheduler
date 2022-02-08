package com.github.krystalics.d10.scheduler.rpc.api;

import com.github.krystalics.d10.scheduler.rpc.base.Rpc;

public interface INodeService {
    @Rpc
    boolean isAlive();
}
