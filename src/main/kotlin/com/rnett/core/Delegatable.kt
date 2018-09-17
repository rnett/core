package com.rnett.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


typealias MakeBacking<BackingType, KeyType, BackingResult> = (ref: DelegatableBy<KeyType, BackingResult>, key: KeyType) -> BackingType

interface DelegatableBy<K, V> : ReadWriteProperty<Any?, V> {
    fun getForDelegate(key: K): V
    fun setForDelegate(key: K, value: V)

    fun fromProperty(prop: KProperty<*>): K = fromPropertyName(prop.name)
    fun fromPropertyName(propertyName: String): K

    class Delegate<K, V>(private val ref: DelegatableBy<K, V>, private val key: K? = null) : ReadWriteProperty<Any?, V> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V {
            return ref.getForDelegate(key ?: ref.fromProperty(property))
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
            ref.setForDelegate(key ?: ref.fromProperty(property), value)
        }
    }

    class GenericDelegate<K, V, R>(private val key: K? = null, private val ref: DelegatableBy<K, V>, val fromValue: (V?) -> R, val toValue: (R) -> V) : ReadWriteProperty<Any?, R> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): R {
            return fromValue(ref.getForDelegate(key ?: ref.fromProperty(property)))
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
            ref.setForDelegate(key ?: ref.fromProperty(property), toValue(value))
        }
    }

    class BackedDelegate<KeyType, BackingResult, Type>(private val key: KeyType? = null, private val ref: DelegatableBy<KeyType, BackingResult>,
                                                       val fromBacking: (BackingResult?) -> Type, val toBacking: (Type) -> BackingResult)
        : ReadWriteProperty<Any?, BackedWrapper<KeyType, BackingResult, Type>> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): BackedWrapper<KeyType, BackingResult, Type> {
            return BackedWrapper(ref, key ?: ref.fromProperty(property), fromBacking, toBacking)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: BackedWrapper<KeyType, BackingResult, Type>) {
            if (key != value.key) throw IllegalArgumentException("Keys of field and BackedWrapper don't match")

            ref.setForDelegate(key ?: ref.fromProperty(property), toBacking(value.value))
        }
    }

    class BackedGenericDelegate<BackingType : GenericBackedWrapper<KeyType, BackingResult, Type>, KeyType, BackingResult, Type>(private val key: KeyType? = null, private val ref: DelegatableBy<KeyType, BackingResult>,
                                                                                                                                val makeBacking: MakeBacking<BackingType, KeyType, BackingResult>)
        : ReadWriteProperty<Any?, BackingType> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): BackingType {
            return makeBacking(ref, key ?: ref.fromProperty(property))
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: BackingType) {
            if (key != value.key && value.key != null) throw IllegalArgumentException("Keys of field and BackedWrapper don't match")

            ref.setForDelegate(key ?: ref.fromProperty(property), value.getBackingValue())
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return getForDelegate(fromProperty(property))
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        setForDelegate(fromProperty(property), value)
    }

    fun by(key: K? = null) = Delegate(this, key)
    val by get() = by(null)

    fun <R> by(fromValue: (V?) -> R, toValue: (R) -> V) = GenericDelegate(null, this, fromValue, toValue)
    fun <R> by(key: K?, fromValue: (V?) -> R, toValue: (R) -> V) = GenericDelegate(key, this, fromValue, toValue)

    fun <R> byBacked(fromValue: (V?) -> R, toValue: (R) -> V) = BackedDelegate(null, this, fromValue, toValue)
    fun <R> byBacked(key: K?, fromValue: (V?) -> R, toValue: (R) -> V) = BackedDelegate(key, this, fromValue, toValue)

    fun <Backing : GenericBackedWrapper<K, V, R>, R> byBacked(makeBacking: MakeBacking<Backing, K, V>) = BackedGenericDelegate(null, this, makeBacking)
    fun <Backing : GenericBackedWrapper<K, V, R>, R> byBacked(key: K?, makeBacking: MakeBacking<Backing, K, V>) = BackedGenericDelegate(key, this, makeBacking)
}

interface DelegatableByString<V> : DelegatableBy<String, V> {
    override fun fromPropertyName(propertyName: String): String = propertyName
}

interface DelegatableStringToString : DelegatableByString<String> {

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

    fun byNumber(key: String? = null) = by(key) { it?.toDouble() ?: 0.0 }
    val byNumber get() = byNumber()
}

interface DelegatableStringToNumber : DelegatableByString<Number> {

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

