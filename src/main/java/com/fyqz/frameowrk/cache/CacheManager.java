package com.fyqz.frameowrk.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheManager {
	Object get(final String key);

	Long leftPush(final String pattern, final Serializable value);

	Long leftPushAll(final String pattern, final Serializable... value);

	Object rightPop(final String pattern);

	List<?> range(final String pattern, long start, long end);

	Long remove(final String pattern, long count, Object value);

	Long size(final String key);

	Set<Object> getAll(final String pattern);

	void set(final String key, final Serializable value, int seconds);

	void set(final String key, final Serializable value);

	Boolean exists(final String key);

	void del(final String key);

	void delAll(final String pattern);

	String type(final String key);

	Boolean expire(final String key, final int seconds);

	Boolean expireAt(final String key, final long unixTime);

	Long ttl(final String key);

	Object getSet(final String key, final Serializable value);

	boolean lock(String key);

	void unlock(String key);

	void hset(String key, Serializable field, Serializable value);

	/**
	 * <p>hash map 批量添加</p>
	 *
	 * @param key redis结构key
	 * @param map 结构实体 map
	 */
	void hPutAll(String key, Map map);

	Long hlen(String key);

	Set<?> hkeys(String key);

	/**
	 * <p>redis hash values值</p>
	 *
	 * @param key redis hash结构名称
	 * @return hash所有值
	 */
	List<?> hValues(String key);

	Map<?, ?> hkv(String key);

	List<Object> hmultGet(String key, Collection<Object> keys);

	Object hget(String key, Serializable field);

	/**
	 * <p>获取hash中指定key的所有值</p>
	 * @param pattern   redis hash结构名称
	 * @param hashKeys     key名称
	 * @return  所有符合条件key
	 */
	List<?> hMultiGet(String pattern, Collection hashKeys);

	void hdel(String key, Serializable field);

	boolean setnx(String key, Serializable value);

	Long incr(String key);

	void setrange(String key, long offset, String value);

	String getrange(String key, long startOffset, long endOffset);

	/**
	 * <p>对应redis结构有序列表</p>
	 *
	 * @param key   redis结构名称
	 * @param value 值
	 */
	void sAdd(String key, Serializable value);

	/**
	 * <p> redis结构set</p>
	 * @param pattern   set名称
	 * @param value set值
	 * @return 集合数量
	 */
	Long sAdd(String pattern, Serializable... value);

	/**
	 * <p>redis结构set</p>
	 * @param pattern   set名称
	 * @param object    删除数据
	 * @return  剩余数量
	 */
	Long sRemove(String pattern, Object object);


	/**
	 * <p>redis结构set</p>
	 * @param pattern   模式
	 * @return  所有set内容
	 */
	Set<?> members(String pattern);

	/**
	 * <p>redis有序列表所有值</p>
	 *
	 * @param key redis结构名称
	 */
	Set<?> sall(String key);

	boolean sdel(String key, Serializable value);

	Object get(String key, Integer expire);

	Set<Object> getAll(String pattern, Integer expire);

	/**
	 * <p>hash中所有的</p>
	 */
	List<?> hgetAll(String pattern);

	Object getFire(String key);

	/**
	 * <p>Description: 从zset中增加带分值的值 </p>
	 *
	 * @param key
	 * @param value
	 * @param score
	 * @return
	 */
	Boolean zAdd(String key, Serializable value, double score);

	Set<?> zrange(String key, long start, long end);
}
