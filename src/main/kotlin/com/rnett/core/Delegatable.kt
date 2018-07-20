package com.rnett.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface StringDelegatable<V> {
    fun get(key: String): V
    fun set(key: String, value: V)

    class Delegate<V>(val ref: StringDelegatable<V>, val key: String? = null) : ReadWriteProperty<Any?, V> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V {
            return ref.get(key ?: property.name)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
            ref.set(key ?: property.name, value)
        }
    }

    fun by(styleName: String? = null) = Delegate(this, styleName)
    val by get() = by(null)
}

interface DelegatableString : StringDelegatable<String> {

    class GenericDelegate<T>(val key: String? = null, val ref: DelegatableString, val fromString: (String?) -> T, val toString: (T) -> String = { it.toString() }) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return fromString(ref.get(key ?: property.name))
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            ref.set(key ?: property.name, toString(value))
        }
    }

    fun <T> by(fromString: (String?) -> T, toString: (T) -> String = { it.toString() }) = GenericDelegate<T>(null, this, fromString, toString)
    fun <T> by(key: String?, fromString: (String?) -> T, toString: (T) -> String = { it.toString() }) = GenericDelegate<T>(key, this, fromString, toString)

    fun byInt(key: String? = null) = by(key, { it?.toInt() ?: 0 })
    val byInt get() = byInt()
    fun byDouble(key: String? = null) = by(key, { it?.toDouble() ?: 0 })
    val byDouble get() = byInt()
}