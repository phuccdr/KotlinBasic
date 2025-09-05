package com.wd.kotlin_basic.task2

import com.wd.kotlin_basic.task1.normalizeName

/**Null Safety**/

// ? nullable
fun createHelloMessage(name: String?): String? {
    if (name.isNullOrEmpty()) return null
    return "Hello $name"
}
// ?:
fun createHelloMessageSafeCall(name: String?): String {
    return createHelloMessage(name) ?: "Hello Kotlin"
}

// !!
fun showHelloMessage(name: String?, companyName: String?) {
    try {
        if (name.isNullOrEmpty()) {
            println(" your name is empty")
        }
        val helloMessage = createHelloMessageSafeCall(name) + " " + companyName!!
        println(helloMessage)
    } catch (e: Exception) {
        println(e.message)
    }
}
// ?.
fun safetyNormalizeName(name: String?): String {
    var result: String? = null
    name?.let {
        result = normalizeName(name)
    }
    return result ?: "Phuc Thai Van"
}

fun main() {
    println(safetyNormalizeName(null))
}



