package com.wd.kotlin_basic.task3.coroutine.coroutinecontext.dispatchers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

/**
 * Custom Dispatcher in IO thread
 */
val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

fun performBackgroundTasks() {

    CoroutineScope(customDispatcher).launch {
        println("Task 1 is running on: ${Thread.currentThread().name}")
        delay(1000)  // Simulate delay (non-blocking)
        println("Task 1 finished")
    }

    CoroutineScope(customDispatcher).launch {
        println("Task 2 is running on: ${Thread.currentThread().name}")
        delay(500)
        println("Task 2 finished")
    }

    CoroutineScope(customDispatcher).launch{
        println("Task 3 is running on: ${Thread.currentThread().name}")
        delay(1000)
        println("Task 3 finished")
    }
}

fun main() = runBlocking {
    performBackgroundTasks()
}