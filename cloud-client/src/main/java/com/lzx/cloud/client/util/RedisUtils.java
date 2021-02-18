package com.lzx.cloud.client.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.security.Key;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName
 * @Description TODO
 * @Date 2021/2/11 14:44
 **/
public class RedisUtils {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public Set<String> Keys(String pattern) {
        Set<String> keys = redisTemplate.keys("");
        if (keys != null && keys.isEmpty()) {
            return keys;
        }
        return null;
    }

    public Boolean deleteByKey(String key) {
        if (StringUtils.isNotBlank(key)) {
            return redisTemplate.delete(key);
        }
        return false;
    }

    public Long deleteByKey(List<String> keys) {
        if (!keys.isEmpty()) {
            return redisTemplate.delete(keys);
        }
        return 0L;
    }

    public List<String> stringMultiGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public void stringMultiSet(Map<String, String> keyValue) {
        if (!keyValue.isEmpty()) {
            redisTemplate.opsForValue().multiSet(keyValue);
        }
    }

    public String stringGet(String key) {
        if (StringUtils.isNotBlank(key)) {
            return redisTemplate.opsForValue().get(key);
        }
        return null;
    }

    public Boolean stringSet(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            redisTemplate.opsForValue().set(key, value);
            return true;
        }
        return false;
    }

    public Boolean stringSet(String key, String value, Duration timeout) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && timeout != null) {
            redisTemplate.opsForValue().set(key, value, timeout);
            return true;
        }
        return false;
    }

    public Boolean stringSet(String key, String value, Long timeout, TimeUnit unit) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && timeout != null && unit != null) {
            redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
            return true;
        }
        return false;
    }

    public Object hashGet(String key, Object hashKey) {
        if (StringUtils.isNotBlank(key) && !Objects.isNull(hashKey)) {
            return redisTemplate.opsForHash().get(key, hashKey);
        }
        return null;
    }

    public List<Object> hashMultiGet(String key, List<Object> hashKeys) {
        if (StringUtils.isNotBlank(key) && !hashKeys.isEmpty()) {
            return redisTemplate.opsForHash().multiGet(key, hashKeys);
        }
        return null;
    }

    public Map<Object, Object> hashGetAll(String key) {
        if (StringUtils.isNotBlank(key)) {
            Map<Object, Object> objectMap = redisTemplate.opsForHash().entries(key);
            return objectMap;
        }
        return null;
    }

    public List<Object> hashVals(String key) {
        if (StringUtils.isNotBlank(key)) {
            List<Object> values = redisTemplate.opsForHash().values(key);
            return values;
        }
        return null;
    }

    public Boolean hashSet(String key, Object hashKey, Object value) {
        if (StringUtils.isNotBlank(key)) {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        }
        return false;
    }

    public Boolean hashMSet(String key, Map<Object,Object> map) {
        if (StringUtils.isNotBlank(key)) {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        }
        return false;
    }

}