package com.wd.kotlin_basic.task3.coroutine

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a Job.
 * The coroutine is cancelled when the resulting job is cancelled.
 * public fun CoroutineScope.launch(
 *     context: CoroutineContext = EmptyCoroutineContext,
 *     start: CoroutineStart = CoroutineStart.DEFAULT,
 *     block: suspend CoroutineScope.() -> Unit
 * ): Job {
 *     val newContext = newCoroutineContext(context)
 *     val coroutine = if (start.isLazy)
 *         LazyStandaloneCoroutine(newContext, block) else
 *         StandaloneCoroutine(newContext, active = true)
 *     coroutine.start(start, coroutine, block)
 *     return coroutine
 * }
 */



fun main(){

}


