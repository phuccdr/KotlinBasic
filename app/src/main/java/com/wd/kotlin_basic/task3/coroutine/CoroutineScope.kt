package com.wd.kotlin_basic.task3.coroutine

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun fetchData1(): List<String> {
    println("FetchData1...")
    delay(1000)
    return listOf("Life", "Work", "Study")
}

suspend fun fetchData2(): List<String> {
    println("FetchData2...")
    delay(5000)
    return listOf("Android", "Flutter", "Kotlin")
}

suspend fun fetchData3() {
    println("FetchData3...")
    delay(1000)
    println("Child Coroutine for coroutine 1")
}



suspend fun main() {
    coroutineScope {
        this.launch {
            println(fetchData1())
            launch {
                fetchData3()
            }
        }
        this.launch {
            println(fetchData2())
        }
    }

}