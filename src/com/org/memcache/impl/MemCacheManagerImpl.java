package com.org.memcache.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mes.framework.core.cache.CacheManager;
import com.org.memcache.MemCacheManager;

@Service("memCacheManager")
public class MemCacheManagerImpl implements MemCacheManager {

    @Resource
    private CacheManager CacheManager;

    @Override
    @SuppressWarnings("unchecked")
    public Map<Object, Object> getData4Cache(String key) {
        Map<Object, Object> list = null;
        Object obj = this.CacheManager.get(key);
        if (null != obj) {
            list = (Map<Object, Object>) obj;
        }
        return list;
    }

    @Override
    public boolean setData4Cache(String key, Map<Object, Object> valueMap) {
        boolean flag = false;
        try {
            this.CacheManager.set(key, valueMap);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean deleteData4Cache(String key) {
        boolean flag = false;
        try {
            this.CacheManager.delete(key);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
