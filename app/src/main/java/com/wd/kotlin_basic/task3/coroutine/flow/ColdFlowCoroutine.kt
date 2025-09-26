package com.wd.kotlin_basic.task3.coroutine.flow

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(){
    runBlocking {
        println(Thread.currentThread().name)
        launch{

        }
    }
}