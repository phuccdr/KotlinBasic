package com.wd.kotlin_basic.task3.coroutine.coroutinecontext.dispatchers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Dispatchers.Default:
 * Toi uu cho cac tac vu dung nhieu CPU: tinh toan nang
 * So luong thread bang so luong core cua CPU (>=2)
 * Su dung: tinh toan, xu ly bitmap, sort, filter
 */

/**
 * Dispatchers.Main:
 * Luong don: UI thread, Update UI
 */

/**
 * Dispatchers.IO: read file, network call
 * Sử dụng nhóm luồng được chia sẻ được tạo theo yêu cầu,
 * với kích thước mặc định là 64 trên JVM, có thể cấu hình theo nhu cầu cụ thể.
 * Fetching data from a database, reading/writing files, or network requests,
 */

fun main() {
    runBlocking {
        repeat(100) {
            if (it % 2 == 0) {
                launch(Dispatchers.IO) {
                    println("Coroutine1: $it, running on ${Thread.currentThread().name}")
                }
            } else {
                launch(Dispatchers.Default) {
                    println("Coroutine2: $it running on ${Thread.currentThread().name}")
                }
            }
        }
// Main Thread la luong don.
        launch{
            for(i in 0..100){
                println("coroutine1(main thread): $i")
            }
        }

        launch{
            for(i in 0..100){
                println("coroutine2(main thread): $i")
            }
        }
    }
}