package com.zoothii.iwbtodojava.core.utulities.caching;

import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.ehcache.EhCacheCacheManager;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        var tenSecondsCache = new CacheConfiguration();
        tenSecondsCache.setName("ten-seconds-cache");
        //tenSecondsCache.setMemoryStoreEvictionPolicy("LRU"); // default LRU so can be commented
        tenSecondsCache.setMaxEntriesLocalHeap(10000);
        tenSecondsCache.setTimeToLiveSeconds(10);

        var tenMinutesCache = new CacheConfiguration();
        tenMinutesCache.setName("ten-minutes-cache");
        //tenMinutesCache.setMemoryStoreEvictionPolicy("LRU");
        tenMinutesCache.setMaxEntriesLocalHeap(10000);
        tenMinutesCache.setTimeToLiveSeconds(600);

        var config = new net.sf.ehcache.config.Configuration();
        config.addCache(tenSecondsCache);
        config.addCache(tenMinutesCache);
        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }
}
