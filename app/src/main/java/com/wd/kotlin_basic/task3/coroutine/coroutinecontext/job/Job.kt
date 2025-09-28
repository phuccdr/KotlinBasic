package com.wd.kotlin_basic.task3.coroutine.coroutinecontext.job

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Hierarchy Structure
 */
suspend fun main() {
    coroutineScope {
        val parentJob: Job = launch {
            println("Parent Job starting")
            launch {
                delay(1000)
                println("Child 1")
            }
            launch {
                println("Child 2")
            }
        }
        parentJob.cancel()
        delay(1000)
        println("Parent Job cancel")
    }
    /** Khi throw Exception coroutineScope bi huy ngay
     * nen mac du coroutine con dung Job rieng khong phu thuoc job cha nhung van bi huy.
     */
    coroutineScope {
        val parentJob2: Job = launch(Dispatchers.IO) {
            println("Parent2 starting")
            launch {
                println("Child(Job2): Starting")
                throw Exception("Error")
            }
            launch(Job()) {
                delay(500)
                println("Child2(Job): Done")
            }
        }
        delay(1000)
    }
}