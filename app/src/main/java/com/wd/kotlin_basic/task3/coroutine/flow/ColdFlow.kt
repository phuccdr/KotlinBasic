package com.wd.kotlin_basic.task3.coroutine.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.math.log
import kotlin.random.Random

/**
 * Flow: Tra ve stream du lieu khong dong bo.
 * Cold flow chi chay khi co collect: Khong gay ton tai nguyen khi chua lang nghe collect.
 * Moi lan collect doc lap.
 * Goi collect o thread nao emit chay tren thread do.
 */
fun getAllCategory(): Flow<List<String>> = flow {
    val categories = listOf("Life", "Work", "Study", "Hello", "Hhihi", "hahaha")
    for (i in 0..10) {
        val start = Random.nextInt(categories.size)
        val end = Random.nextInt(start, categories.size)
        delay(1000)
        emit(categories.subList(start, end))
    }
}

fun fetchData(): Flow<String> = flow {
    for (i in 0..10) {
        emit("oke")
        emit(i.toString())
    }
}

/**
 *flowOn dung de thay doi context noi emit flow
 */

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100) // giả lập công việc tốn CPU
        println("Emitting $i")
        emit(i) // phát ra giá trị
    }
}.flowOn(Dispatchers.Default) // chạy emit ở background thread


suspend fun main() {
    val flow = getAllCategory()
    flow.filter {
        it.contains("Study")
    }.collect {
        println(it)
    }
    println("getAllCategory done")

    fetchData().collect {
        println(it)
    }
    /**
     * Timeout: chi cho phep collect trong 2000 ms
     */
    println("Collect with withTimeout")
    withTimeout(3000) {
        flow.collect {
            println(it)
        }
    }
    /**
     * collect tren main thread nhung do dung flowOn nen emit chay tren background thread
     */
    runBlocking {
        simple().collect { value -> println(value) }
    }

    /**
     * Con nhieu phan khac trong flow nua :(...
     */

    println("main done")
}