package com.wd.kotlin_basic


/**1. Cú pháp cơ bản của Kotlin**/
fun sumInt(a: Int, b: Int): Int {
    return a + b
}

fun mod(a: Int, b: Int): Int {
    return a % b
}

fun compareInt(a: Int, b: Int): Int {
    return a.compareTo(b)
}

fun toStringForDouble(a: Double): String {
    return a.toString()
}

fun floor(a: Double): Int {
    return a.toInt()
}

fun normalizeName(name: String): String {
    val tmp: List<String> = name.trim().lowercase().split(" ")
    val result = tmp.map {
        upperCaseForFirstChar(it)
    }.joinToString(separator = " ")
    println("normalizeName from $name to $result")
    return result
}

/** viet hoa ki tu dau cho 1 tu **/
fun upperCaseForFirstChar(a: String): String {
    val s = a.trim()
    return s.substring(0, 1).uppercase() + s.substring(1)
}


/** 2. Cấu trúc điều kiện và vòng lặp **/

fun checkPassword(password: String, confirmPassword: String): Boolean {
    if (!password.equals(confirmPassword) || password.length < 8) return false
    return false
}

fun getDayName(day: Int): String {
    return when (day) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6 -> "Saturday"
        7 -> "Sunday"
        else -> "Invalid day"
    }
}

fun printlnFruits(fruits: List<String>) {
    for (fruit in fruits) {
        println(fruit)
    }
    for (index in fruits.indices) {
        println(fruits[index])
    }

    fruits.forEach {
        println(it)
    }
    fruits.forEachIndexed { index, fruit ->
        println("$index $fruit")
    }

    for (index in 0..fruits.size - 1 step 2) {
        println(fruits[index])
    }

    for (index in fruits.size - 1 downTo 0) {
        println(fruits[index])
    }
}

fun convertDecimalToBinary(a: Int): String? {
    if (a <= 0) return null
    var number = a
    var arrBinary: MutableList<Int> = mutableListOf()
    while (number > 0) {
        arrBinary.add(number % 2)
        number = number / 2
    }
    arrBinary.reverse()
    return arrBinary.joinToString("")
}


/** 3.Collection (Danh sách dữ liệu)
 * Collection<T> is the root of the collection hierarchy.
 * **/


fun printlnAll(a: Collection<String>) {
    for (s in a) {
        println(s)
    }
}

fun intersectList(a: List<Int>, b: List<Int>): Set<Int> {
    val a2 = a.toSet()
    val b2 = b.toSet()
    return a2.intersect(b2)
}

fun frequencyCounter(a: List<Int>) {
    val map = mutableMapOf<Int, Int>()
    a.forEach {
        map[it] = (map[it] ?: 0) + 1
    }
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

fun sumList(a: Set<Int>): Int {
    return a.reduce { acc, i -> acc + i }
}

fun chooseFruits(fruits: List<String>): List<String> {
    return fruits.filter { it.first().equals('a') }
        .map { it.uppercase() }
        .sortedBy { it }

}

/**  Hàm & Extension Function **/

fun String.normalize(): String {
    return this.trim().lowercase()
}

// Extension function
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    if (index1 !in this.indices || index2 !in this.indices) {
        println("Invalid index")
        return
    }
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

// Generic function
fun <T> add(a: T, b: T): String {
    return a.toString() + b.toString()
}

// Default Parameter
fun printHelloMessage(name: String = "Eco Mobile") {
    println("Hello $name")
}

fun sum2(a: Int, b: Int) = a + b


/** Null Safety **/
fun safetyNormalizeName(name: String?): String {
    var result :String?= null
   name?.let{
       result = normalizeName(name)
   }
    return result?:"Phuc Thai Van"
}








fun main() {
    println(normalizeName("   pHuc ThaI huu  "))
}