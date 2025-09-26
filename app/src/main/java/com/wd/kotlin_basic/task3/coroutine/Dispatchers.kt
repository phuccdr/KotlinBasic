package com.wd.kotlin_basic.task3.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun main() {
    coroutineScope {
        repeat(100) {
            if (it % 2 == 0) {
                launch(Dispatchers.IO) {
                    println("Hello $it, running on ${Thread.currentThread().name}")
                }
            } else {
                launch(Dispatchers.Default) {
                    println("Hello $it running on ${Thread.currentThread().name}")
                }
            }
        }
    }
}