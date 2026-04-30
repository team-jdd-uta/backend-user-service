package com.teamuta.userinfoserver.config;

public final class CustomerShardContext {

    public static final String SHARD_3307 = "SHARD_3307";
    public static final String SHARD_3309 = "SHARD_3309";

    private static final ThreadLocal<String> CURRENT_SHARD = new ThreadLocal<>();

    private CustomerShardContext() {
    }

    public static void useShard(String shardKey) {
        CURRENT_SHARD.set(shardKey);
    }

    public static String getCurrentShard() {
        return CURRENT_SHARD.get();
    }

    public static void clear() {
        CURRENT_SHARD.remove();
    }
}