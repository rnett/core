package com.rnett.core

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun CoroutineScope.launchIn(
        context: CoroutineContext = this.coroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
): Job = this.launch(context, start, block)

fun <T> CoroutineScope.asyncIn(
        context: CoroutineContext = this.coroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
): Deferred<T> = this.async(context, start, block)

suspend fun <K, V> Map<K, Deferred<V>>.awaitValues() = mapValues { it.value.await() }

suspend inline fun <T> Iterable<T>.mapAndJoinAll(block: (T) -> Job) = map(block).joinAll()
suspend inline fun <T, R> Iterable<T>.mapAndAwaitAll(block: (T) -> Deferred<R>) = map(block).awaitAll()

suspend inline fun <T> Iterable<T>.launchAndJoinAll(scope: CoroutineScope, crossinline block: suspend CoroutineScope.(T) -> Unit) = mapAndJoinAll { scope.launch { block(it) } }
suspend inline fun <T> Iterable<T>.launchInAndJoinAll(scope: CoroutineScope, crossinline block: suspend CoroutineScope.(T) -> Unit) = mapAndJoinAll { scope.launchIn { block(it) } }

suspend inline fun <T, R> Iterable<T>.asyncAndAwaitAll(scope: CoroutineScope, crossinline block: suspend CoroutineScope.(T) -> R) = mapAndAwaitAll { scope.async { block(it) } }
suspend inline fun <T, R> Iterable<T>.asyncInAndAwaitAll(scope: CoroutineScope, crossinline block: suspend CoroutineScope.(T) -> R) = mapAndAwaitAll { scope.asyncIn { block(it) } }

suspend inline fun <K, V, R> Map<K, V>.mapAndAwaitAllValues(block: (Map.Entry<K, V>) -> Deferred<R>) = mapValues(block).awaitValues()