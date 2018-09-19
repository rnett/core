package com.rnett.core

abstract class BackedWrapper<KeyType, BackingResult, Type>(private val backing: DelegatableBy<KeyType, BackingResult>? = null, internal val key: KeyType?) {

    abstract fun fromBacking(backing: BackingResult?): Type
    abstract fun toBacking(value: Type): BackingResult

    private var temp: BackingResult? = null

    val backed get() = backing != null && key != null

    //TODO doesn't update on internal update, e.g. adding to a mutable list
    protected var backingValue
        get() = fromBacking(if (backed) backing!!.getForDelegate(key!!) else temp)
        set(value) {
            if (backed)
                backing!!.setForDelegate(key!!, toBacking(value))
            else
                temp = toBacking(value)
        }

    internal fun getBackingValue() = toBacking(backingValue)

    open operator fun plusAssign(value: Type) {
        this.backingValue = value
    }
}

open class DirectBackedWrapper<KeyType, BackingResultType>(backing: DelegatableBy<KeyType, BackingResultType>? = null, key: KeyType?, val ifNull: BackingResultType)
    : BackedWrapper<KeyType, BackingResultType, BackingResultType>(backing, key) {
    override fun fromBacking(backing: BackingResultType?): BackingResultType = backing ?: ifNull

    override fun toBacking(value: BackingResultType): BackingResultType = value
}

class StandardBackedWrapper<KeyType, BackingResult, Type>(backing: DelegatableBy<KeyType, BackingResult>? = null, key: KeyType,
                                                          internal val fromBackingFun: (BackingResult?) -> Type, internal val toBackingFun: (Type) -> BackingResult)
    : BackedWrapper<KeyType, BackingResult, Type>(backing, key) {

    override fun fromBacking(backing: BackingResult?): Type = fromBackingFun(backing)
    override fun toBacking(value: Type): BackingResult = toBackingFun(value)

    var value
        get() = backingValue
        set(v) {
            backingValue = v
        }
}