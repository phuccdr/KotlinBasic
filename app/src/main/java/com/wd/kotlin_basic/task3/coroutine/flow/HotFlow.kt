package com.wd.kotlin_basic.task3.coroutine.flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout

/**
 * Hot Flow: StateFlow, SharedFlow.
 * Luon emit du lieu khong phu thuoc viec co collect hay khong.
 * Dung Emit cac du lieu, su kien lien tuc
 */
/**
 * StateFlow/ MutableStateFlow. Collector nhan du lieu khi co thay doi.(UI state)
 */
suspend fun stateFlow() = coroutineScope {
    val _stateFlow = MutableStateFlow("Hello")
    val stateFlow = _stateFlow.asStateFlow()
    launch {
        stateFlow.collect {
            println("Collector thu 1 nhan duoc gia tri emit: $it")
        }
    }
    delay(1000)
    _stateFlow.emit("hihi")
    delay(1000)
    launch {
        stateFlow.collect {
            println("Collector thu 2 nhan duoc gia tri emit: $it")
        }
    }
    _stateFlow.emit("hahaha")
}

/**
 * Shared Flow: Phat su kien 1 lan, khong giu gia tri cu, collector khong nhan gia tri cu
 * Khi collector lang nghe sau khi emit du lieu thi se khong nhan duoc gia tri.
 * Phu hop de lang nghe cac event tranh viec collector sau nhan duoc du lieu da xu ly trc do.
 */
suspend fun sharedFlow() = coroutineScope {
    val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()
    launch {
        sharedFlow.collect {
            println("Collector thu 1 nhan duoc gia tri emit: $it")
        }
    }
    delay(1000)
    _sharedFlow.emit("Success")
    delay(1000)
    launch {
        sharedFlow.collect {
            println("Collector thu 2 da nhan gia tri emit: $it")
        }
    }
    delay(100) // Thoi gian de collector thu 2 duoc thiet lap
    _sharedFlow.emit("Hehe")
}

// Dung try catch de bat loi timeout vi neu khong collector no se lang nghe mai lam coroutine khong ket thuc duoc
suspend fun main() {
    println("stateFlow starting")
    try {
        withTimeout(5000) {
            stateFlow()
        }
    } catch (e: Exception) {
        println(e.message)
    }

    println("sharedFlow starting")
    try {
        withTimeout(5000) {
            sharedFlow()
        }
    } catch (e: Exception) {
        println(e.message)
    }
}