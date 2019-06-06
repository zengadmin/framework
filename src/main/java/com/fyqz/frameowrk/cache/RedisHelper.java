package com.fyqz.frameowrk.cache;

import com.fyqz.frameowrk.util.InstanceUtil;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存辅助类
 *
 * @author zengchao
 * @version 2016年4月2日 下午4:17:22
 */
public final class RedisHelper implements CacheManager {
	private RedisSerializer<String> keySerializer;
	private RedisSerializer<Object> valueSerializer;
	private RedisTemplate<Serializable, Serializable> redisTemplate;
	private final Integer EXPIRE =21600;

	@SuppressWarnings("unchecked")
	public void setRedisTemplate(RedisTemplate<Serializable, Serializable> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
		this.valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
		CacheUtil.setCacheManager(this);
	}

	public RedisTemplate<Serializable, Serializable> getRedisTemplate() {
		return redisTemplate;
	}

	@Override
	public final Object get(final String key) {
		return redisTemplate.boundValueOps(key).get();
	}

	@Override
	public final Object get(final String key, Integer expire) {
		expire(key, expire);
		return redisTemplate.boundValueOps(key).get();
	}

	@Override
	public final Object getFire(final String key) {
		expire(key, EXPIRE);
		return redisTemplate.boundValueOps(key).get();
	}

	@Override
	public final Set<Object> getAll(final String pattern) {
		Set<Object> values = InstanceUtil.newHashSet();
		Set<Serializable> keys = redisTemplate.keys(pattern);
		for (Serializable key : keys) {
			values.add(redisTemplate.opsForValue().get(key));
		}
		return values;
	}

	@Override
	public final Set<Object> getAll(final String pattern, Integer expire) {
		Set<Object> values = InstanceUtil.newHashSet();
		Set<Serializable> keys = redisTemplate.keys(pattern);
		for (Serializable key : keys) {
			expire(key.toString(), expire);
			values.add(redisTemplate.opsForValue().get(key));
		}
		return values;
	}

	@Override
	public final void set(final String key, final Serializable value, int seconds) {
		redisTemplate.boundValueOps(key).set(value);
		expire(key, seconds);
	}

	@Override
	public final void set(final String key, final Serializable value) {
		redisTemplate.boundValueOps(key).set(value);
		//redisTemplate.opsForValue().multiGet()
		expire(key, EXPIRE);
	}

	@Override
	public final Boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public final void del(final String key) {
		redisTemplate.delete(key);
	}

	@Override
	public final void delAll(final String pattern) {
		redisTemplate.delete(redisTemplate.keys(pattern));
	}

	@Override
	public final String type(final String key) {
		return redisTemplate.type(key).getClass().getName();
	}

	/**
	 * 在某段时间后失效
	 *
	 * @return
	 */
	@Override
	public final Boolean expire(final String key, final int seconds) {
		return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
	}

	/**
	 * 在某个时间点失效
	 *
	 * @param key
	 * @param unixTime
	 * @return
	 */
	@Override
	public final Boolean expireAt(final String key, final long unixTime) {
		return redisTemplate.expireAt(key, new Date(unixTime));
	}

	@Override
	public final Long ttl(final String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	@Override
	public final void setrange(final String key, final long offset, final String value) {
		redisTemplate.boundValueOps(key).set(value, offset);
	}

	@Override
	public final String getrange(final String key, final long startOffset, final long endOffset) {
		return redisTemplate.boundValueOps(key).get(startOffset, endOffset);
	}

	@Override
	public final Object getSet(final String key, final Serializable value) {
		return redisTemplate.boundValueOps(key).getAndSet(value);
	}

	@Override
	public boolean setnx(String key, Serializable value) {
		RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
		RedisConnection redisConnection = null;
		try {
			redisConnection = RedisConnectionUtils.getConnection(factory);
			if (redisConnection == null) {
				return redisTemplate.boundValueOps(key).setIfAbsent(value);
			}
			return redisConnection.setNX(keySerializer.serialize(key), valueSerializer.serialize(value));
		} finally {
			if (redisConnection != null) {
				RedisConnectionUtils.releaseConnection(redisConnection, factory);
			}
		}
	}

	@Override
	public boolean lock(String key) {
		RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
		RedisConnection redisConnection = null;
		try {
			redisConnection = RedisConnectionUtils.getConnection(factory);
			if (redisConnection == null) {
				return redisTemplate.boundValueOps(key).setIfAbsent("0");
			}
			return redisConnection.setNX(keySerializer.serialize(key), valueSerializer.serialize("0"));
		} finally {
			if (redisConnection != null) {
				RedisConnectionUtils.releaseConnection(redisConnection, factory);
			}
		}
	}

	@Override
	public void unlock(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void hset(String key, Serializable field, Serializable value) {
		redisTemplate.boundHashOps(key).put(field, value);
	}

	@Override
	public void hPutAll(String key, Map map) {
		redisTemplate.boundHashOps(key).putAll(map);
	}

	@Override
	public Object hget(String key, Serializable field) {
		return redisTemplate.boundHashOps(key).get(field);
	}

	@Override
	public List<?> hMultiGet(String pattern, Collection hashKeys) {
		return redisTemplate.boundHashOps(pattern).multiGet(hashKeys);
	}

	@Override
	public void hdel(String key, Serializable field) {
		redisTemplate.boundHashOps(key).delete(field);
	}

	@Override
	public Long incr(String key) {
		return redisTemplate.boundValueOps(key).increment(1L);
	}

	@Override
	public void sAdd(String key, Serializable value) {

	}

	@Override
	public Long sAdd(String pattern, Serializable... value) {
		return redisTemplate.boundSetOps(pattern).add(value);
	}

	@Override
	public Long sRemove(String pattern, Object object) {
		return redisTemplate.boundSetOps(pattern).remove(object);
	}

	@Override
	public Set<?> members(String pattern) {
		return redisTemplate.boundSetOps(pattern).members();
	}

	@Override
	public Set<?> sall(String key) {
		return null;
	}

	@Override
	public boolean sdel(String key, Serializable value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<?> hgetAll(String pattern) {
		return redisTemplate.boundHashOps(pattern).values();
	}

	@Override
	public Long hlen(String key) {
		return redisTemplate.boundHashOps(key).size();
	}

	@Override
	public Set<?> hkeys(String key) {
		return redisTemplate.boundHashOps(key).keys();
	}

	@Override
	public List<?> hValues(String key) {
		return redisTemplate.boundHashOps(key).values();
	}

	/**
	 * 获取所有键值对
	 */
	@Override
	public Map<?, ?> hkv(String key) {
		return redisTemplate.boundHashOps(key).entries();
	}

	@Override
	public Long leftPush(String pattern, Serializable value) {
		return redisTemplate.boundListOps(pattern).leftPush(value);
	}

	@Override
	public Long leftPushAll(String pattern, Serializable... value) {
		return redisTemplate.boundListOps(pattern).leftPushAll(value);
	}

	@Override
	public Object rightPop(String pattern) {
		return redisTemplate.boundListOps(pattern).rightPop();
	}

	@Override
	public List<?> range(String pattern, long start, long end) {
		return redisTemplate.boundListOps(pattern).range(start, end);
	}

	@Override
	public Long remove(String pattern, long count, Object value) {
		return redisTemplate.boundListOps(pattern).remove(count, value);
	}

	@Override
	public Long size(String key) {
		return redisTemplate.boundListOps(key).size();
	}

	@Override
	public List<Object> hmultGet(String key, Collection<Object> keys) {
		return redisTemplate.boundHashOps(key).multiGet(keys);
	}

	@Override
	public Boolean zAdd(String key, Serializable value, double score) {
		return redisTemplate.boundZSetOps(key).add(value, score);
	}

	@Override
	public Set<?> zrange(String key, long start, long end) {

		return redisTemplate.boundZSetOps(key).range(start, end);
	}

}
