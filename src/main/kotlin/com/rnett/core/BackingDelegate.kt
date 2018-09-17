package com.rnett.core

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

class FieldBacking<V>(val field: KMutableProperty<V>) : ReadWriteProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return field.getter.call()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        field.setter.call(value)
    }
}

class ReadOnlyFieldBacking<V>(val field: KProperty<V>) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return field.getter.call()
    }
}


inline fun <V> backingVar(field: KMutableProperty<V>) = FieldBacking(field)
inline fun <V> backingVal(field: KProperty<V>) = ReadOnlyFieldBacking(field)