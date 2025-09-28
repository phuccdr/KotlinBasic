package com.wd.kotlin_basic.task3.coroutine.coroutinecontext

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * CoroutineExceptionHandler: dung de xu ly cac exception trong coroutine ma try catch chua bat
 */
private val handler = CoroutineExceptionHandler { _, exception ->
   // Log.e(TAG, "Coroutine exception", exception)
    println("Coroutine $exception")
}
private val scope = CoroutineScope(Dispatchers.IO + handler + SupervisorJob())

suspend fun fetchData(){
    println("FetchData starting")
    delay(1000)
    throw Exception("Error")
}


suspend fun main() {
    coroutineScope {
        scope.launch {
            fetchData()
        }
    }.join()
}