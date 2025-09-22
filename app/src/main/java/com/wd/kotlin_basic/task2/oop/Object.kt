package com.wd.kotlin_basic.task2.oop

/**
 * Object declaration
 */
object CacheData {
    val data = hashSetOf("kotlin")
    fun checkContain(value: String): Boolean {
        return data.contains(value)
    }
}

fun main(){
    println(CacheData.checkContain("kotlin"))
}