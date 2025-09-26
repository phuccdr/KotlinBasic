package com.wd.kotlin_basic.task3.coroutine

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * Thread duoc quan ly boi he dieu hanh. Thread co the chay song song dua tren core CPU.
 * Khi tao mot thread OS cap phat bo nho cho no va su dung nhan de chuyen doi giua cac thread
 * 1 thread thuong can 2MB nen JVM thuong chi chay song song 2000 thread.
 */

/**
 * Một coroutine không bị ràng buộc với một luồng cụ thể.
 * Nó có thể tạm dừng trên một luồng và tiếp tục trên luồng khác, vì vậy nhiều coroutine có thể chia sẻ cùng một nhóm luồng.
 * Khi một coroutine tạm dừng, luồng không bị chặn và vẫn tự do thực hiện các tác vụ khác.
 * Điều này làm cho coroutine nhẹ hơn rất nhiều so với luồng và cho phép chạy hàng triệu coroutine trong một tiến trình mà không làm cạn kiệt tài nguyên hệ thống.
 */
suspend fun createCoroutines(){
    coroutineScope{
        repeat(2000){
            launch{
                println("Coroutine $it is running")
                // delay(1000)
            }
        }
    }
}

fun createThreads(){
    repeat(2000){
        thread{
            Thread.sleep(1000)
            println("Thread $it is running")
        }
    }
}

suspend fun testSwitchThreadInCoroutine(){
    val threadNames: MutableSet<String> = mutableSetOf()
    coroutineScope {
        repeat(100){
            launch {
                println("Thread ${Thread.currentThread().name}")
                threadNames.add(Thread.currentThread().name)
            }
        }
    }
    println("Da co ${threadNames.size} thread tham gia")
}
suspend fun main(){
    println("-----------------------Test switch thread in coroutine----------------------------------------")
    testSwitchThreadInCoroutine()
   println("-----------------------Start create coroutines----------------------------------------")
    createCoroutines()
    delay(1000)
    println("-----------------------Success create coroutines----------------------------------------")
    createThreads()
    println("-----------------------Success create threads----------------------------------------")

}