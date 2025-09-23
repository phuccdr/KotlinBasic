package com.wd.kotlin_basic.task3.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * suspend/resume
 *  It allows a running operation to pause and resume later without affecting the structure of your code.
 */
suspend fun fetchSkills() {
    println("Fetching Skills...")
    delay(1000)
    println(listOf("Android", "Flutter", "Kotlin"))
}

suspend fun fetchTechnical() {
    println("Fetching Technical...")
    delay(500)
    println(listOf("Java", "C++", "C#"))

}

/**
 * Coroutine gọi tới suspend function.
 * Khi gặp một điểm suspension point (vd: delay, withContext, await), coroutine lưu lại trạng thái (stack, biến cục bộ).
 * Thread được nhả ra để chạy công việc khác.
 * Khi kết quả đã sẵn sàng, coroutine được resume (tiếp tục) tại đúng chỗ bị suspend, giống như chưa từng bị dừng.
 */
fun main() {
    val measureTime1 = measureTimeMillis {
        runBlocking {
            fetchSkills()
            fetchTechnical()
        }
    }
    println("MeasureTime1: $measureTime1")
    val measureTime2 = measureTimeMillis {
        runBlocking {
            launch {
                fetchSkills()
            }
            launch {
                fetchTechnical()
            }
        }
    }
    println("MeasureTime2: $measureTime2")
}

