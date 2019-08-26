package com.keebraa.telegraph.lib;

import static java.time.Instant.now;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This map wait configured time to get value in case if it does not available
 * for now, or returns the value immediately if it exists.
 * 
 * @author vvasianovych
 *
 */
public class PoolMap extends ConcurrentHashMap<String, MicroserviceDescriptor> {

    private long getTimeout;

    public PoolMap(long getTimeout) {
        this.getTimeout = getTimeout;
    }

    @Override
    public boolean containsKey(Object key) {
        return super.get(key) != null;
    }

    @Override
    public MicroserviceDescriptor get(Object key) {
        long callTime = now().toEpochMilli();
        long currentTime = callTime;
        MicroserviceDescriptor value = null;
        while (value == null && currentTime - callTime < getTimeout) {
            currentTime = Instant.now().toEpochMilli();
            value = super.get(key);
        }
        return value;
    }
}
