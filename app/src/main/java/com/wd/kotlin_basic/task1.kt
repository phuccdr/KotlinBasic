package com.wd.kotlin_basic

import java.lang.Math.pow


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

fun calculator(a: Double, b: Double) {
    println("a+b = ${a + b}")
    println("a-b = ${a - b}")
    println("a*b = ${a * b}")
    println("a/b = ${a / b}")
    println("a%b = ${a % b}")
    println("a^b = ${pow(a, b)}")
    println("a.toInt() = ${a.toInt()}")
    if (a == b) println("a==b")
    else if (a > b) println("a>b")
    else println("a<b")
    if (a > 0 && b > 0) println("a,b > 0")
}

fun compareInt(a: String, b: String): Boolean {
    val c1 = a.toDouble()
    val c2 = b.toDouble()
    return c1 == c2
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

/** String templates **/
fun showStringTemplates(string: String) {
    println("$string length is ${string.length}")
}


/** String formatting **/
fun showStringFormatting(a: Int, b: Int) {
    println(String.format("%d + %d = %d", a, b, a + b))
    val pi = 3.14159
    println(String.format("%.2f", pi))
}


/** Multi-dollar String Interpolation **/
//fun showMultiDollarString() {
//
//    val simpleName = "Phuc"
//    val qualifiedName = "Thai Huu Phuc"
//    val string = $$"""
//    {
//      "$schema": "https://json-schema.org/draft/2020-12/schema",
//      "$id": "https://example.com/product.schema.json",
//      "$dynamicAnchor": "meta",
//      "title": "$${simpleName ?: qualifiedName ?: "unknown"}",
//      "type": "object"
//    }
//    """
//
//    println(string)
//}

/** Multiline strings **/
fun showMutilineString() {
    val message = """
        Kotlin
        Android
        Phuc
    """.trimIndent()
    println(message)
}


/** 2. Cấu trúc điều kiện và vòng lặp **/


/** if else **/
fun checkPassword(password: String, confirmPassword: String): Boolean {
    if (!password.equals(confirmPassword) || password.length < 8) return false
    return false
}

/** when **/
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

/** for **/
fun printlnFruits(fruits: List<String>) {
    for (fruit in fruits) {
        println(fruit)
    }
    for (index in fruits.indices) {
        println(fruits[index])
    }
    for ((index, fruit) in fruits.withIndex()) {
        println("$index: $fruit")
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

/** while**/
fun convertDecimalToBinary(a: Int): String? {
    if (a <= 0) return null
    var number = a
    val arrBinary: MutableList<Int> = mutableListOf()
    while (number > 0) {
        arrBinary.add(number % 2)
        number = number / 2
    }
    arrBinary.reverse()
    return arrBinary.joinToString("")
}

/** do while **/
fun showAllList() {
    var pos = 8
    do {
        println(pos)
        pos--
    } while (pos > 0)
}


/** 3.Collection (Danh sách dữ liệu)
 * Collection<T> is the root of the collection hierarchy.
 * **/


fun syntaxForCollections(a:Collection<Int>, b:Collection<Int>){
    println(a.union(b))
    println(a.intersect(b))
    println(a.subtract(b))
    println(a.contains(4))
    println(a.containsAll(b))
    println(a.isEmpty())
    println(a.size)
    println(a.iterator())
}

fun printlnAll(a: Collection<String>) {
    for (s in a) {
        println(s)
    }
}


/** List và mutableList **/
fun syntaxForList() {
    val numbers = mutableListOf(1, 2, 3, 4)
    numbers.add(5)
    numbers.removeAt(1)
    numbers[0] = 0
    numbers.shuffle()
    println(numbers)
    val numbers2 = listOf(2, 5, 3, 6, 7, 9)
    val number3 = numbers2 + numbers
    println(number3)
    println(number3.sorted())
    println(number3.sortedDescending())
}


/** Set va mutableSet **/

fun syntaxForSet() {
    val numbers = mutableSetOf(1, 2, 3, 4)
    numbers.add(5)
    numbers.add(2)
    numbers.remove(4)
    println(numbers)
    val set2 = setOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
    println("$numbers union $set2 = ${numbers.union(set2)}}")
    println("$numbers intersect $set2 = ${numbers.intersect(set2)}}")
}

fun intersectList(a: List<Int>, b: List<Int>): Set<Int> {
    val a2 = a.toSet()
    val b2 = b.toSet()
    return a2.intersect(b2)
}

/** Map và mutableMap **/
fun frequencyCounter(a: List<Int>) {
    val map = mutableMapOf<Int, Int>()
    a.forEach {
        map[it] = (map[it] ?: 0) + 1
    }
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

/** Cac ham quan trong trong Collections **/

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
    var result: String? = null
    name?.let {
        result = normalizeName(name)
    }
    return result ?: "Phuc Thai Van"
}

fun main() {
//    println(normalizeName("   pHuc ThaI huu  "))
//    showMutilineString()
val a : Collection<Int> = listOf(1,2,3)
    println(a.iterator())
}