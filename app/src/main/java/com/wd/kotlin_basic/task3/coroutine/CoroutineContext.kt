package com.wd.kotlin_basic.task3.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Khi tao 1 coroutine moi bang cach .launch hoac .async no tu dong tao cho minh:
 * +) Job: Theo doi lifecycle cua coroutine ...
 * +) CoroutineDispatcher: Quyet dinh coroutine chay tren thread loai nao.
 * +) CoroutineExceptionHandle: Xu ly cac exception trong coroutine
 * Job la gi ?
 * SupervisorJob la gi ? Khac gi Job ?
 *
 */
suspend fun main() {
    demoChildJob()
}

suspend fun demoChildJob() {
    println("-------------Job-------------")
    val job = Job()
    val scope = CoroutineScope(Dispatchers.Default + job)
    val job1 = scope.launch {
        for (i in 1..10) {
            launch {
                println("Job: $i running on ${Thread.currentThread().name}")
                delay(500)
            }
        }
    }
    job1.join()

    println("-------------SupervisorJob-------------")
    val supervisorJob = SupervisorJob()
    val scope2 = CoroutineScope(Dispatchers.IO + supervisorJob)
    val job2 = scope2.launch {
        for (i in 1..10) {
            launch {
                if (i == 5) throw Exception("Error")
                println("SupervisorJob: $i running on ${Thread.currentThread().name}")
            }
        }
    }
    job2.join()
}

