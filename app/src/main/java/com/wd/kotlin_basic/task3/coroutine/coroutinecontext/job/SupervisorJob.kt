package com.wd.kotlin_basic.task3.coroutine.coroutinecontext.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * SupervisorJob: khi coroutine con loi khong anh huong toi cac thanh phan cua coroutine cha.
 */
private val supervisorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

suspend fun fetchData1() {
    println("FetchData1 starting")
    delay(1000)
    throw Exception("Error")

}

suspend fun fetchData2(){
    println("FetchData2 starting")
    delay(1000)
    println("FetchData2 done")
}

fun main() = runBlocking {
    val job = supervisorScope.launch {
        launch {
            try {
                fetchData1()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        launch{
            fetchData2()
        }
    }
    
    job.join()
    println("main done")
}