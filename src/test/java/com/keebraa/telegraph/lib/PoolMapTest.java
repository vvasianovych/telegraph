package com.keebraa.telegraph.lib;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

public class PoolMapTest {

    @Test
    public void get_immediatelyCase() {
        PoolMap map = new PoolMap(1000);
        map.put("test", new MicroserviceDescriptor());

        assertNotNull(map.get("test"));
        assertEquals(1, map.size());
        assertTrue(map.containsKey("test"));
    }

    @Test
    public void get_timedOutNullCase() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newFixedThreadPool(3);
        final PoolMap map = new PoolMap(2000);
        Future<MicroserviceDescriptor> future = es.submit(() -> {
            return map.get("test");
        });
        sleep(3000);
        map.put("test", new MicroserviceDescriptor());
        MicroserviceDescriptor result = future.get();
        assertNull(result);
    }

    @Test
    public void get_timedOutNormalCase() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newFixedThreadPool(3);
        final PoolMap map = new PoolMap(5000);
        Future<MicroserviceDescriptor> future = es.submit(() -> {
            return map.get("test");
        });
        sleep(1000);
        map.put("test", new MicroserviceDescriptor());
        MicroserviceDescriptor result = future.get();
        assertNotNull(result);
    }
}
