package com.rnett.core

class MovingAverage() : Number() {

    private var currentAverage = 0.0
    private var currentCount = 0.0

    val average get() = currentAverage

    operator fun invoke() = average

    constructor(values: Collection<Number>) : this() {
        add(values)
    }

    fun add(value: Number) {
        currentAverage = (currentAverage * currentCount + value.toDouble()) / (++currentCount)
    }

    fun add(vararg values: Number) {
        values.forEach { add(it) }
    }

    fun add(values: Collection<Number>) {
        values.forEach { add(it.toDouble()) }
    }


    operator fun plus(value: Number): MovingAverage {
        add(value); return this
    }

    operator fun plus(value: Collection<out Number>): MovingAverage {
        add(value); return this
    }


    override fun toByte(): Byte = currentAverage.toByte()

    override fun toChar(): Char = currentAverage.toChar()

    override fun toDouble(): Double = currentAverage.toDouble()

    override fun toFloat(): Float = currentAverage.toFloat()

    override fun toInt(): Int = currentAverage.toInt()

    override fun toLong(): Long = currentAverage.toLong()

    override fun toShort(): Short = currentAverage.toShort()

    override fun toString(): String {
        return currentAverage.toString()
    }
}