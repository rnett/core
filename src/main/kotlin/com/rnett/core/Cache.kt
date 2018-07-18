package com.rnett.core

import com.kizitonwose.time.Interval
import java.util.*

class Cache<K, V>(val timeout: Long = -1, val default: (K) -> V) : Map<K, V> {

    constructor(timeout: Interval<*>, default: (K) -> V) : this(timeout.inMilliseconds.longValue, default)

    private val map = mutableMapOf<K, Pair<Long, V>>()

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

        map[key] = Pair(Calendar.getInstance().timeInMillis, default(key))
        return map[key]!!.second
    }

    fun put(key: K, value: V) {
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
