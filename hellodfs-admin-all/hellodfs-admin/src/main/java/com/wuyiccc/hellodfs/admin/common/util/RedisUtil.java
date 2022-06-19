package com.wuyiccc.hellodfs.admin.common.util;

/**
 * @author wuyiccc
 * @date 2020/10/23 20:39
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Reids工具类
 */
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean keyIsExist(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 以秒为单位，返回给定key的剩余存活时间(TTL Time To Live)
     * @param key
     * @return
     */
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 为key设置存活时间
     * @param key
     * @param timeout
     */
    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 对value + delta
     * @param key
     * @param delta
     * @return
     */
    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 对value - delta
     * @param key
     * @param delta
     * @return
     */
    public long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 查找所有符合给定模式pattern的key
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 删除一个key
     * @param key
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置 key-value(字符串)
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置key-value(字符串) + 过期时间
     * @param key
     * @param value
     * @param timeout
     */
    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置key-value(字符串) + 60s过期时间, 如果存在，则不进行操作
     * @param key
     * @param value
     */
    public void setnx60s(String key, String value) {
        redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
    }

    /**
     * 设置key-value(字符串)， 如果存在，则不进行操作
     * @param key
     * @param value
     */
    public void setnx(String key, String value) {
        redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 返回key所关联的字符串
     * @param key
     * @return
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 根据传入的key列表批量查询
     * @param keys
     * @return
     */
    public List<String> mget(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量查询，管道pipeline
     * @param keys
     * @return
     */
    public List<Object> batchGet(List<String> keys) {

        List<Object> result = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection src = (StringRedisConnection)connection;

                for (String k : keys) {
                    src.get(k);
                }
                return null;
            }
        });

        return result;
    }

    /**
     * HSET key field value  将哈希白哦key中的域field设置为value
     * @param key
     * @param field
     * @param value
     */
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * HGET key field, 返回hash表中给定域field的值
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * HDEL key field [field ...] 删除hash表中的一个或多个指定域，不存在的域将被忽略
     * @param key
     * @param fields
     */
    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * HGETALL key 返回hash表key中所有的域和值
     * @param key
     * @return
     */
    public Map<Object, Object> hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * LPUSH key value 将value插入到list的左边
     * @param key
     * @param value
     * @return
     */
    public long lpush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * LPOP key， 移除并返回列表key的头元素
     * @param key
     * @return
     */
    public String lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * RPUSH key value 将一个值value插入到key的表尾
     * @param key
     * @param value
     * @return
     */
    public long rpush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 根据redisKey, 获取对应的值的数字, 如果没有, 则返回0
     * @param redisKey
     * @return
     */
    public Integer getIntegerFromRedis(String redisKey) {
        String integerStr = this.get(redisKey);
        if (StringUtils.isBlank(integerStr)) {
            integerStr = "0";
        }
        return Integer.valueOf(integerStr);
    }



}
