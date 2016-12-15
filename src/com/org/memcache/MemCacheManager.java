package com.org.memcache;

import java.util.Map;

public interface MemCacheManager {

    public Map<Object, Object> getData4Cache(String key);

    public boolean setData4Cache(String key, Map<Object, Object> valueMap);

    public boolean deleteData4Cache(String key);
}
