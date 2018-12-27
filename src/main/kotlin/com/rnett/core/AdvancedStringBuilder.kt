package com.rnett.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class AdvancedStringBuilder(var prepend: String = "", var append: String = "", var linebreak: String = "\n") {
    private val internal = StringBuilder()

    fun appendPure(value: Any) = append(value, true)
    fun appendlnPure(value: Any) = appendln(value, true)

    fun append(value: Any, pure: Boolean = false) {
        if (pure) internal.append(value) else internal.append("$prepend$value$append")
    }

    fun appendln(value: Any, pure: Boolean = false) {
        append(value, pure); internal.append(linebreak)
    }

    fun append(vararg values: Any, pure: Boolean = false) = values.forEach {
        append(it, pure)
    }

    fun appendln(vararg values: Any, pure: Boolean = false) = values.forEach {
        append(it, pure)
    }

    @ExperimentalContracts
    inline fun codeBlock(builder: AdvancedStringBuilder.() -> Unit) {
        if (toString().endsWith("\n")) // on a new line, indent
            stepIn("\t", "{", "}", builder)
        else
            stepInPure("\t", "{$append", "$prepend}$append", builder)
    }

    operator fun Any.unaryPlus() = appendln(this)
    operator fun Any.unaryMinus() = append(this)

    @ExperimentalContracts
    inline operator fun String.invoke(open: String = "", close: String = "", pure: Boolean = false, builder: AdvancedStringBuilder.() -> Unit) =
            if (pure)
                stepInPure(this, open, close, builder)
            else
                stepIn(this, open, close, builder)

    override fun toString() = internal.toString()
}

@ExperimentalContracts
inline fun AdvancedStringBuilder.stepInPure(addToStart: String, open: String = "", close: String = "", builder: AdvancedStringBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    appendlnPure(open)
    val new = AdvancedStringBuilder(prepend + addToStart, append, linebreak)
    new.apply(builder)
    appendPure(new.toString())
    appendlnPure(close)
}

@ExperimentalContracts
inline fun AdvancedStringBuilder.stepIn(addToStart: String, open: String = "", close: String = "", builder: AdvancedStringBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    stepInPure(addToStart, prepend + open + append, prepend + close + append, builder)
}

@ExperimentalContracts
inline fun advancedBuildString(prepend: String = "", append: String = "", linebreak: String = "\n", builder: AdvancedStringBuilder.() -> Unit): String {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return AdvancedStringBuilder(prepend, append, linebreak).apply(builder).toString()
}

inline fun advancedBuildStringNoContract(prepend: String = "", append: String = "", linebreak: String = "\n", builder: AdvancedStringBuilder.() -> Unit): String =
        AdvancedStringBuilder(prepend, append, linebreak).apply(builder).toString()

