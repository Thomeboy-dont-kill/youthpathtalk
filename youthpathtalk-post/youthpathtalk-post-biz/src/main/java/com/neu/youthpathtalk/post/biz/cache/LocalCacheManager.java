package com.neu.youthpathtalk.post.biz.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Julien
 * @time 2026/04/09 22:01
 * @description 本地缓存管理
 */
@Component
public class LocalCacheManager {
    private final Cache<Long,Boolean> existsCache;

    public LocalCacheManager(){
        this.existsCache= Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    public Boolean getExists(Long postId){
        return existsCache.getIfPresent(postId);
    }

    public void putExists(Long postId,Boolean exists){
        existsCache.put(postId,exists);
    }

    public void invalidateExists(Long postId){
        existsCache.invalidate(postId);
    }
    public void invalidateExistsBatch(Collection<Long> postIds){
        existsCache.invalidateAll(postIds);
    }
}
