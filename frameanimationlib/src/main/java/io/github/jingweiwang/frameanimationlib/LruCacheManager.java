/*
 * Copyright 2017 JingweiWang
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
import android.util.LruCache;

class LruCacheManager<K, V> {
    private LruCache<K, V> lruCache;

    LruCacheManager() {
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int mCacheSize = maxMemory / 4;
        lruCache = new LruCache<K, V>(mCacheSize) {
            @Override
            protected int sizeOf(K key, V value) {
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

    V put(K key, V value) {
        return lruCache.put(key, value);
    }

    void clearLruCache() {
        if (lruCache != null) {
            if (lruCache.size() > 0) {
                lruCache.evictAll();
            }
        }
    }
}
