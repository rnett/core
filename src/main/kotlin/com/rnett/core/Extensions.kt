package com.rnett.core

@Suppress("UNCHECKED_CAST")
inline fun <E : Any> Iterable<*>.safeCast() = this.mapNotNull {
    try {
        it as? E
    } catch (e: Exception) {
        null
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <E : Any> Sequence<*>.safeCast() = this.mapNotNull {
    try {
        it as? E
    } catch (e: Exception) {
        null
    }
}