package nl.kingdev.graphqlclient.util;

import java.util.*;
import java.util.function.Supplier;

public class Cache<T> {

    private long ttl;

    private Timer timer = new Timer();

    public Cache(long ttl) {
        this.ttl = ttl;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cache.entrySet().removeIf(next -> isExpired(next.getValue()));
            }
        }, 0, 60000);
    }

    private static class CacheObject<T> {
        protected T object;
        protected long ttl;

        public CacheObject(T object, long ttl) {
            this.object = object;
            this.ttl = ttl;
        }
    }

    private Map<String, CacheObject<T>> cache = new HashMap<>();


    public Cache<T> store(String key, T object) {
        cache.put(key, new CacheObject<>(object, System.currentTimeMillis() + ttl));
        return this;
    }


    public T remember(String key, Supplier<T> supplier) {
        if (has(key)) {
            return cache.get(key).object;
        }
        T value = supplier.get();
        this.store(key, value);
        return value;
    }

    private boolean isExpired(CacheObject<T> object) {
        return object.ttl >= System.currentTimeMillis();
    }

    public boolean has(String key) {
        CacheObject<T> cacheObject = cache.get(key);

        if (cacheObject != null && isExpired(cacheObject)) {
            return true;
        }
        return false;
    }

    public T get(String key) {
        CacheObject<T> cacheObject = cache.get(key);
        if (has(key)) {
            return cacheObject.object;
        }

        return null;
    }

}
