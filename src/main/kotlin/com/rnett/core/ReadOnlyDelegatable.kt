package com.rnett.core

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ReadOnlyDelegatableBy<K, V> : ReadOnlyProperty<Any?, V> {
    fun getForDelegate(key: K): V

    fun fromProperty(prop: KProperty<*>): K = fromPropertyName(prop.name)
    fun fromPropertyName(propertyName: String): K

    class Delegate<K, V>(private val ref: ReadOnlyDelegatableBy<K, V>, private val key: K? = null) : ReadOnlyProperty<Any?, V> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V {
            return ref.getForDelegate(key ?: ref.fromProperty(property))
        }
    }

    class GenericDelegate<K, V, R>(private val key: K? = null, private val ref: ReadOnlyDelegatableBy<K, V>, val fromValue: (V?) -> R, val toValue: (R) -> V) : ReadOnlyProperty<Any?, R> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): R {
            return fromValue(ref.getForDelegate(key ?: ref.fromProperty(property)))
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return getForDelegate(fromProperty(property))
    }

    fun by(key: K? = null) = Delegate(this, key)
    val by get() = by(null)

    fun <R> by(fromValue: (V?) -> R, toValue: (R) -> V) = GenericDelegate(null, this, fromValue, toValue)
    fun <R> by(key: K?, fromValue: (V?) -> R, toValue: (R) -> V) = GenericDelegate(key, this, fromValue, toValue)

}

interface ReadOnlyDelegatableByString<V> : ReadOnlyDelegatableBy<String, V> {
    override fun fromPropertyName(propertyName: String): String = propertyName
}

interface ReadOnlyDelegatableStringToString : ReadOnlyDelegatableByString<String> {

    fun <R> by(fromString: (String?) -> R) = by(null, fromString)
    fun <R> by(key: String?, fromString: (String?) -> R) = by(key, fromString, { it.toString() })

    fun byInt(key: String? = null) = by(key) { it?.toInt() ?: 0 }
    val byInt get() = byInt()

    fun byFloat(key: String? = null) = by(key) { it?.toFloat() ?: 0.0.toFloat() }
    val byFloat get() = byFloat()

    fun byLong(key: String? = null) = by(key) { it?.toLong() ?: 0.toLong() }
    val byLong get() = byLong()

    fun byDouble(key: String? = null) = by(key) { it?.toDouble() ?: 0.0 }
    val byDouble get() = byDouble()

    fun byByte(key: String? = null) = by(key) { it?.toByte() ?: 0.toByte() }
    val byByte get() = byByte()

    fun byChar(key: String? = null) = by(key) { it?.toInt()?.toChar() ?: 0.toChar() }
    val byChar get() = byChar()

    fun byShort(key: String? = null) = by(key) { it?.toShort() ?: 0.toShort() }
    val byShort get() = byShort()
}

interface ReadOnlyDelegatableStringToNumber : ReadOnlyDelegatableByString<Number> {

    fun byInt(key: String? = null) = by(key, { it?.toInt() ?: 0 }, { it })
    val byInt get() = byInt()

    fun byFloat(key: String? = null) = by(key, { it?.toFloat() ?: 0.0.toFloat() }, { it })
    val byFloat get() = byFloat()

    fun byLong(key: String? = null) = by(key, { it?.toLong() ?: 0.toLong() }, { it })
    val byLong get() = byLong()

    fun byDouble(key: String? = null) = by(key, { it?.toDouble() ?: 0.0 }, { it })
    val byDouble get() = byDouble()

    fun byByte(key: String? = null) = by(key, { it?.toByte() ?: 0.toByte() }, { it })
    val byByte get() = byByte()

    fun byChar(key: String? = null) = by(key, { it?.toChar() ?: 0.toChar() }, { it.toInt() })
    val byChar get() = byChar()

    fun byShort(key: String? = null) = by(key, { it?.toShort() ?: 0.toShort() }, { it })
    val byShort get() = byShort()
}
