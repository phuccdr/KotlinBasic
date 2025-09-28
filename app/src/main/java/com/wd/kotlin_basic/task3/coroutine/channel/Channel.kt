package com.wd.kotlin_basic.task3.coroutine.channel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Channel: giao tiep giua cac coroutine.
 *Cacsc courtine no chay song song gio muon 2 coroutine muon truyen du lieu cho nhau thi sinh ra channel
 * Producer gui du lieu vao channel, consumer doc tu channel
 * ham send producer la suspend function, Channe gui du lieu gui theo co che FIFO (first in first out )
 */
fun fetchData(): String {
    val data = listOf("Android", "Flutter", "Kotlin")
    return data.random()
}

fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) send(x * x)
}
suspend fun main() {
    coroutineScope {
        val dataChannel = Channel<String>()
        launch {
            while (true) {
                val newData = fetchData()
                dataChannel.send(newData)
            }
        }
        launch {
            for (data in dataChannel) {
                println("Received: $data")
            }
        }

        val squares = produceSquares()
        squares.consumeEach { println(it) }
    }
}