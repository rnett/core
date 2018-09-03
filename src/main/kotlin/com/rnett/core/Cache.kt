package com.rnett.core

import com.kizitonwose.time.Interval
import java.util.*

class Cache<K, V>(val timeout: Long = -1, val sizeLimit: Int = -1, val default: (K) -> V) : Map<K, V> {

    constructor(timeout: Interval<*>, sizeLimit: Int = -1, default: (K) -> V) : this(timeout.inMilliseconds.longValue, sizeLimit, default)

    private val map = mutableMapOf<K, Pair<Long, V>>()

    private val byAge = LinkedList<K>()

    override val size: Int
        get() = map.size

    override fun containsKey(key: K): Boolean =
            if (map.contains(key)) {
                if (map[key]!!.isValid())
                    true
                else {
                    map.remove(key)
                    false
                }
            } else
                false

    override fun containsValue(value: V): Boolean = map.mapValues { it.value.second }.containsValue(value)

    override fun get(key: K): V {
        if (map.containsKey(key) && map[key]!!.isValid())
            return map[key]!!.second

        if (sizeLimit > 0 && size >= sizeLimit - 1) {
            if (byAge.isNotEmpty())
                map.remove(byAge.pop())
        }

        put(key, default(key))
        return map[key]!!.second
    }

    fun put(key: K, value: V) {
        byAge.add(key)
        map[key] = Pair(Calendar.getInstance().timeInMillis, value)
    }

    operator fun set(key: K, value: V) {
        put(key, value)
    }

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: Set<Map.Entry<K, V>>
        get() = map.mapValues { it.value.second }.entries
    override val keys: Set<K>
        get() = map.keys
    override val values: Collection<V>
        get() = map.mapValues { it.value.second }.values

    fun clear() = map.clear()

    fun reset(key: K) {
        map.remove(key)
    }

    private fun Pair<Long, V>.isValid() = (timeout <= 0 || Calendar.getInstance().timeInMillis - this.first <= timeout)

}

class ManualCache<K, V>(val timeout: Long = -1, val sizeLimit: Int = -1) : Map<K, V> {

    constructor(timeout: Interval<*>, sizeLimit: Int = -1) : this(timeout.inMilliseconds.longValue, sizeLimit)

    private val map = mutableMapOf<K, Pair<Long, V>>()

    private val byAge = LinkedList<K>()

    override val size: Int
        get() = map.size

    override fun containsKey(key: K): Boolean =
            if (map.contains(key)) {
                if (map[key]!!.isValid())
                    true
                else {
                    map.remove(key)
                    false
                }
            } else
                false

    override fun containsValue(value: V): Boolean = map.mapValues { it.value.second }.containsValue(value)

    override fun get(key: K): V? {
        if (map[key] != null && map[key]!!.isValid())
            return map[key]!!.second

        if (sizeLimit > 0 && size >= sizeLimit - 1) {
            if (byAge.isNotEmpty())
                map.remove(byAge.pop())
        }

        return map[key]?.second
    }

    fun put(key: K, value: V) {
        byAge.add(key)
        map[key] = Pair(Calendar.getInstance().timeInMillis, value)
    }

    operator fun set(key: K, value: V) {
        put(key, value)
    }

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: Set<Map.Entry<K, V>>
        get() = map.mapValues { it.value.second }.entries
    override val keys: Set<K>
        get() = map.keys
    override val values: Collection<V>
        get() = map.mapValues { it.value.second }.values

    fun clear() = map.clear()

    fun reset(key: K) {
        map.remove(key)
    }

    private fun Pair<Long, V>.isValid() = (timeout <= 0 || Calendar.getInstance().timeInMillis - this.first <= timeout)

}