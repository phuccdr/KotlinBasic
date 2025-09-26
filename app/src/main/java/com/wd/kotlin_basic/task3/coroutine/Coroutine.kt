package com.wd.kotlin_basic.task3.coroutine

import com.wd.kotlin_basic.task3.SealedClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun fetchDataCategory(): Flow<SealedClass<List<String>>> = flow {
    emit(SealedClass.Loading)
    delay(2000)
    emit(SealedClass.Success(listOf("Life", "Work", "Study")))
}

fun fetchIcons(): Flow<SealedClass<List<String>>> = flow {
    emit(SealedClass.Loading)
    delay(1000)
    emit(SealedClass.Success(listOf("Hehe", "Hhih", "hahaha")))
}

fun main() {
    runBlocking {
        launch(Dispatchers.IO) {
            fetchDataCategory().collect {
                if (it is SealedClass.Loading) {
                    println("Loading fetchDataCategory()")
                }
                if (it is SealedClass.Success) {
                    println(it.data)
                }
                if (it is SealedClass.Error) {
                    println("Error fetchDataCategory")
                }
            }
        }

        launch {
            fetchIcons().collect {
                if (it is SealedClass.Loading) {
                    println("Loading fetchIcons()")
                }
                if (it is SealedClass.Success) {
                    println(it.data)
                }
                if (it is SealedClass.Error) {
                    println("Error fetchIcons")
                }
            }
        }
        coroutineScope { }
    }
}