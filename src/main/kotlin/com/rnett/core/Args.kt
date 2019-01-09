package com.rnett.core

object Args {
    fun extract(flag: String, args: Array<String>): String? =
            args.indexOf(flag).let {
                if (it == -1 || it == args.lastIndex)
                    null
                else
                    args[it + 1]
            }

    fun <T> extract(flag: String, args: Array<String>, func: (String) -> T) = extract(flag, args)?.let(func)
}

infix fun Array<String>.extract(flag: String) = Args.extract(flag, this)
operator fun Array<String>.get(flag: String) = Args.extract(flag, this)

fun <T> Array<String>.extract(flag: String, func: (String) -> T) = Args.extract(flag, this, func)
operator fun <T> Array<String>.get(flag: String, func: (String) -> T) = Args.extract(flag, this, func)