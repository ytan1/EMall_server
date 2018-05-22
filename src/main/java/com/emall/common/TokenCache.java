package com.emall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {
    //for log exceptions for guava cache
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    private static LoadingCache<String, String> localCaches = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //The cacheLoader implement method "load" to return a value if the entry not exists when query
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static String getKey(String key) {
        String value = null;
        try {

            value = localCaches.get(key);
            //"null" may be returned by cacheLoader, avoid a Guava exception if return null directly!
            if(value.equals("null")){
                return null;
            }
            return value;

        }catch(Exception e){
            logger.error("localcaches error", e);
        }
        return null;
    }

    public static void setKey(String key, String token){
        localCaches.put(key, token);
    }

    public static void removeKey(String key){
        localCaches.invalidate(key);
    }


}
