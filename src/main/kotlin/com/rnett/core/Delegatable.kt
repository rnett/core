package com.rnett.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface StringDelegatable<V> : ReadWriteProperty<Any?, V> {
    fun getForDelegate(key: String): V
    fun setForDelegate(key: String, value: V)

    fun fromProperty(prop: KProperty<*>): String = fromPropertyName(prop.name)
    fun fromPropertyName(propertyName: String): String = propertyName

    class Delegate<V>(private val ref: StringDelegatable<V>, private val key: String? = null) : ReadWriteProperty<Any?, V> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V {
            return ref.getForDelegate(key ?: ref.fromProperty(property))
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
            ref.setForDelegate(key ?: ref.fromProperty(property), value)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return getForDelegate(fromProperty(property))
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        setForDelegate(fromProperty(property), value)
    }

    fun by(styleName: String? = null) = Delegate(this, styleName)
    val by get() = by(null)
}

interface DelegatableString : StringDelegatable<String> {

    class GenericDelegate<T>(private val key: String? = null, private val ref: DelegatableString, val fromString: (String?) -> T, val toString: (T) -> String = { it.toString() }) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return fromString(ref.getForDelegate(key ?: ref.fromProperty(property)))
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            ref.setForDelegate(key ?: ref.fromProperty(property), toString(value))
        }
    }

    fun <T> by(fromString: (String?) -> T, toString: (T) -> String = { it.toString() }) = GenericDelegate<T>(null, this, fromString, toString)
    fun <T> by(key: String?, fromString: (String?) -> T, toString: (T) -> String = { it.toString() }) = GenericDelegate<T>(key, this, fromString, toString)

    fun byInt(key: String? = null) = by<Int>(key, { it?.toInt() ?: 0 })
    val byInt get() = byInt()

    fun byDouble(key: String? = null) = by<Double>(key, { it?.toDouble() ?: 0.0 })
    val byDouble get() = byDouble()

    fun byNumber(key: String? = null) = by<Number>(key, { it?.toDouble() ?: 0.0 })
    val byNumber get() = byNumber()
}