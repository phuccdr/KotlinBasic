package com.wd.kotlin_basic.task3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun fetchDataCategory(): Flow<Result<List<String>>> = flow {
    emit(Result.Loading)
    delay(2000)
    emit(Result.Success(listOf("Life", "Work", "Study")))
}

fun fetchIcons(): Flow<Result<List<String>>> = flow {
    emit(Result.Loading)
    delay(1000)
    emit(Result.Success(listOf("Hehe", "Hhih", "hahaha")))
}

suspend fun fetchDescription(): List<String> {
    delay(1000)
    println("Fetching description...")
    return listOf("Android", "Flutter", "Kotlin")
}

fun main() {
    runBlocking {
        launch(Dispatchers.IO) {
            fetchDataCategory().collect {
                if (it is Result.Loading) {
                    println("Loading fetchDataCategory()")
                }
                if (it is Result.Success) {
                    println(it.data)
                }
                if (it is Result.Error) {
                    println("Error fetchDataCategory")
                }
            }
        }

        launch {
            fetchIcons().collect {
                if (it is Result.Loading) {
                    println("Loading fetchIcons()")
                }
                if (it is Result.Success) {
                    println(it.data)
                }
                if (it is Result.Error) {
                    println("Error fetchIcons")
                }
            }
        }

        coroutineScope {  }
        val description = async { fetchDescription() }
        println(description.await())



    }
}