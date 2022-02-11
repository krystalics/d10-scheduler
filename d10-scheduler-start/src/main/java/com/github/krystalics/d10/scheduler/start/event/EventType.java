package com.github.krystalics.d10.scheduler.start.event;

public enum EventType {
    LIVE_NODE_ADD, LIVE_NODE_DEL,
    LEADER_ADD, LEADER_DEL,
    SHARD_ADD, SHARD_DEL,SHARD_CHANGE,
    CONNECTION_LOST, RECONNECTION
}
