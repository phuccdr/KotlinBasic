package com.wd.kotlin_basic.task3

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun connectServer() {
    delay(5000)
    println("Connect server ....")
}

fun login1() {
    println("Login 1 ....")
}

fun login2() {
    println("Login 2...")
}

suspend fun performLogin() {
    coroutineScope {
        connectServer()
        launch {
            login1()
        }
        launch {
            login2()
        }
        println("Login success")
    }
}

//fun main() {
//    runBlocking {
//        performLogin()
//    }
//}

class Test(var b:Int){
    val a: Int get() = b

}
fun main() {
    val test = Test(3)
    println(test.a)
    test.b = 3
    println(test.a)
}