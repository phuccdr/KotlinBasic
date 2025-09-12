package com.wd.kotlin_basic.task1

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
    if (a == b) {
        println("a==b")
    } else if (a > b) {
        println("a>b")
    } else {
        println("a<b")
    }
    if (a > 0 && b > 0) {
        println("a,b > 0")
    }
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

/**  Hàm & Extension Function **/
fun sum2(a: Int, b: Int) = a + b

fun main() {
//    val set :Set<Int> = setOf(0,1,2,6,3,4,4,5)
//
//    val iter = set.iterator()
//    for( i in set){
//        println(i)
//    }
//
    val mutable = mutableListOf(1, 2, 3)
    val realOnly: List<Int> = mutable
    mutable.add(4)
    println(realOnly) // [1, 2, 3, 4] -> vẫn có thể thay đổi được

//    var a1= listOf(1,2,3)
//    var b1:MutableList<Int> = a1
//    a1.add(4)
//    println(b1)

}