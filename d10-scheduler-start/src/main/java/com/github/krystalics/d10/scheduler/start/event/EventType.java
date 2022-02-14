package com.github.krystalics.d10.scheduler.start.event;

public enum EventType {
    LIVE_NODE_ADD, LIVE_NODE_DEL,
    LEADER_ADD, LEADER_DEL,
    SHARD_BEGIN, SHARD_CHANGE, SHARD_DEL,
    CONNECTION_LOST, RECONNECTION
}
