package com.rnett.core

class MovingAverage() : Number() {

    private var currentTotal = 0.0
    private var currentCount = 0.0

    val average get() = currentTotal / currentCount
    val count get() = currentCount
    val total get() = currentTotal

    operator fun invoke() = average

    constructor(values: Collection<Number>) : this() {
        add(values)
    }

    constructor(vararg values: Number) : this() {
        add(values.toList())
    }

    infix fun add(value: Number) {
        currentTotal += value.toDouble()
        currentCount += 1
    }

    fun add(vararg values: Number) {
        values.forEach { add(it) }
    }

    infix fun add(values: Iterable<Number>) {
        values.forEach { add(it.toDouble()) }
    }

    /**
     * Adds [value].second [value].first times
     */
    infix fun add(value: Pair<Number, Number>) {
        currentTotal += value.first.toDouble() * value.second.toDouble()
        currentCount += value.first.toDouble()
    }

    /**
     * Adds [value].second [value].first times
     */
    fun add(vararg values: Pair<Number, Number>) {
        values.forEach(::add)
    }

    @JvmName("addIterablePairs")
    infix fun add(values: Iterable<Pair<Number, Number>>) {
        values.forEach { add(it) }
    }

    operator fun plus(value: Number): MovingAverage {
        add(value); return this
    }

    operator fun plus(value: Pair<Number, Number>): MovingAverage {
        add(value); return this
    }

    operator fun plus(value: Iterable<Number>): MovingAverage {
        add(value); return this
    }

    @JvmName("plusIterablePairs")
    operator fun plus(value: Iterable<Pair<Number, Number>>): MovingAverage {
        add(value); return this
    }


    operator fun component1() = average
    operator fun component2() = count
    operator fun component3() = total

    override fun toByte(): Byte = average.toByte()

    override fun toChar(): Char = average.toChar()

    override fun toDouble(): Double = average.toDouble()

    override fun toFloat(): Float = average.toFloat()

    override fun toInt(): Int = average.toInt()

    override fun toLong(): Long = average.toLong()

    override fun toShort(): Short = average.toShort()

    override fun toString(): String {
        return average.toString()
    }
}