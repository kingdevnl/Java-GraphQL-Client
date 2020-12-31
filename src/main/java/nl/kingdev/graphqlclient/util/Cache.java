/*
 * MIT License
 *
 * Copyright (c) 2020 KingdevNL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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



    public void close() {
        this.timer.cancel();
    }
}
