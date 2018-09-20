/*
 * Copyright 2018 JingweiWang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jingweiwang.frameanimationlib;

import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

class LruCacheManager<K, V> {
    private final LruCache<K, V> lruCache;

    LruCacheManager(int distributionRate) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        final int mCacheSize = maxMemory / distributionRate; // 总容量的大小为当前进程可用内存的(1/distributionRate)
        lruCache = new LruCache<K, V>(mCacheSize) {
            @Override
            protected int sizeOf(@NonNull K key, @NonNull V value) {
                if (value instanceof BitmapDrawable) {
                    return ((BitmapDrawable) value).getBitmap().getByteCount();
                } else {
                    return super.sizeOf(key, value);
                }
            }
        };
    }

    V get(K key) {
        return lruCache.get(key);
    }

    void put(K key, V value) {
        lruCache.put(key, value);
    }

    void clearLruCache() {
        if (lruCache != null) {
            if (lruCache.size() > 0) {
                lruCache.evictAll();
            }
        }
    }

    int getMaxSize() {
        return lruCache.maxSize();
    }

    int getSize() {
        return lruCache.size();
    }

    String printLruCache() {
        return lruCache.toString();
    }
}
