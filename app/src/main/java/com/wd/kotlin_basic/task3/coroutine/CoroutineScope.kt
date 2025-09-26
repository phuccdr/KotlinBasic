package com.wd.kotlin_basic.task3.coroutine

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Defines the coroutine's lifecycle and provides the coroutine context.
 * CoroutineScope: dung de quan ly cac nhieu cac coroutine theo he thong phan cap. quan ly lifecycle cua cac coroutine con
 * Một coroutine cha sẽ chờ tất cả coroutine con hoàn thành trước khi nó kết thúc.
 * Nếu coroutine cha thất bại hoặc bị hủy, thì tất cả coroutine con của nó cũng sẽ bị hủy theo cách đệ quy.
 */
suspend fun fetchData1(): List<String> {
    println("FetchData1...")
    delay(1000)
    return listOf("Life", "Work", "Study")
}

suspend fun fetchData2(): List<String> {
    println("FetchData2...")
    delay(2000)
    return listOf("Android", "Flutter", "Kotlin")
}

suspend fun fetchData3() {
    println("FetchData3...")
    delay(500)
    println("Child Coroutine FetchData1")
}


@OptIn(DelicateCoroutinesApi::class)
suspend fun main() {
    coroutineScope {
        this.launch {
            println(fetchData1())
            fetchData3()
            println("coroutine 1 done")
        }
        this.launch {
            println(fetchData2())
        }
    }


    println("---------Coroutine2 starting--------------")
    coroutineScope {
        launch {
            println("coroutine 1 is running")
            delay(1000)
            println("coroutine 1 done")
        }
        val task2 = launch {
            println("coroutine 2 is running")
            delay(1000)
            launch {
                println("child coroutine task2 is running")
            }
            println("coroutine 2 done")
        }
        task2.cancel()
    }
    println("main done")

    GlobalScope.launch {
        println("coroutine 1 is running")
        delay(1000)
        println("coroutine 1 done")

    }
}