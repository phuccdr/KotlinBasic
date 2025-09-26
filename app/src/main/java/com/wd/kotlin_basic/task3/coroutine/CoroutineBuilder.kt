package com.wd.kotlin_basic.task3.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * CoroutineScope.launch()
 * Sử dụng CoroutineScope.launch() để thực hiện một tác vụ song song
 * với các công việc khác khi không cần đến kết quả
 */
suspend fun insertData() {
    coroutineScope {
        launch(Dispatchers.IO) {
            delay(2000)
            println("insert data1 success")
        }
        launch(Dispatchers.IO) {
            delay(1000)
            println("insert data2 success")
        }
    }
}

/**
 * CoroutineScope.async()
 * Neu can tra ve ket qua thi ta su dung async , await de tra ve ket qua thay vi launch.
 *  Async khac launch 1 diem nua la:
 *  Launch nếu có exception nó sẽ throw ra ngay lập tức
 *  Async nếu có exception thì nó sẽ giữ lại đến khi gọi await, nếu không gọi await có thể bị bỏ qua)
 */
suspend fun fetchData() {
    coroutineScope {
        val getData1 = async(Dispatchers.IO) {
            delay(2000)
            listOf("Life", "Work", "Study")
        }
        println("Fetch Data1 Success")
        val getData2 = async(Dispatchers.IO) {
            delay(1000)
            listOf("Android", "Flutter", "Kotlin")
        }
        println(getData1.await())
        println(getData2.await())
    }
}

/**
 * WithContext:
 */
suspend fun fetchData4() {
    withContext(Dispatchers.IO) {
        val task1 = this.async {
            delay(2000)
            listOf(1, 2, 3, 4, 5)
        }
        val task2 = this.async {
            delay(1000)
            listOf(6, 7, 8, 9, 10)
        }
        println(task1.await())
        println(task2.await())
    }
    println("Fetch Data1 Success")
}

/**
 * runBlocking{}
 * Chay tren main thread
 */
suspend fun main() {
    insertData()
    println("----------Insert Data Success-------------")
    fetchData()
    fetchData4()
    println("main done")
}
