package com.hallbooking.utilis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    public <T> T get(String cacheName, String key, Class<T> type) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Cache.ValueWrapper wrapper = cache.get(key);
                if (wrapper != null) {
                    return type.cast(wrapper.get());
                }
            }
        } catch (Exception e) {
            log.warn("Error retrieving from cache {}: {}", cacheName, e.getMessage());
        }
        return null;
    }

    public <T> T get(String cacheName, String key, Callable<T> valueLoader) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                return cache.get(key, valueLoader);
            }
        } catch (Exception e) {
            log.warn("Error retrieving from cache {} with loader: {}", cacheName, e.getMessage());
        }
        
        // Fallback to direct value loading
        try {
            return valueLoader.call();
        } catch (Exception e) {
            log.error("Error in fallback value loading: {}", e.getMessage());
            throw new RuntimeException("Cache retrieval and fallback failed", e);
        }
    }

    public void put(String cacheName, String key, Object value) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.put(key, value);
            }
        } catch (Exception e) {
            log.warn("Error putting to cache {}: {}", cacheName, e.getMessage());
        }
    }

    public void evict(String cacheName, String key) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
            }
        } catch (Exception e) {
            log.warn("Error evicting from cache {}: {}", cacheName, e.getMessage());
        }
    }

    public void clear(String cacheName) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        } catch (Exception e) {
            log.warn("Error clearing cache {}: {}", cacheName, e.getMessage());
        }
    }

    public boolean exists(String cacheName, String key) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                return cache.get(key) != null;
            }
        } catch (Exception e) {
            log.warn("Error checking cache existence {}: {}", cacheName, e.getMessage());
        }
        return false;
    }
}