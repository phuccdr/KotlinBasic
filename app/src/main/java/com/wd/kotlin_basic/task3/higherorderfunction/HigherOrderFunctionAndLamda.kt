package com.wd.kotlin_basic.task3.higherorderfunction

import kotlinx.coroutines.runBlocking
import kotlin.math.pow

/**
 *  typealias: khai bao ten moi cho kieu du lieu
 **/
typealias CategoryMap = HashMap<String, String>

/**
 * function is parameter other function
 */
fun createHelloMessage(f: (String) -> String): String {
    return "MIT" + f("Phuc")
}


/**
 * Higher function
 */
fun demoHigherFunction(
    list: List<Int>,
    result: (Int, Int) -> Int
): Int {
    var sum = 0
    for (i in list) {
        sum += result(i, 2)
    }
    return sum
}


fun pow(a: Int, b: Int): Int {
    return a.toDouble().pow(b.toDouble()).toInt()
}

fun List<String>.printlnFruits(relationship: (String, String) -> String) {
    for (fruit in this) {
        println(relationship(fruit, "is a tasty fruit"))
    }
}

fun clickButton(enableButton: Boolean, onClickPlayButton: () -> Unit) {
    if (enableButton) {
        onClickPlayButton()
    }
}

/**
 * Anonymous Function
 */
fun convertStringToInt(num: String): Int {
    return num.toIntOrNull() ?: 0
}

fun helloMessage(name: String): String {
    return "Hello $name"
}

/** Inline function
 * code ham inline duoc chen vao code cua ham bao ngoai
 *  Trong higher-order function moi ham (lambda ) là 1 object va se giu closeure ( scope cua function)
 * bo nho cung cap cho higher order gay lang phi va giam hieu suat
 * Inline Function co the giai quyet duoc nhc diem nay
 * **/
inline fun playVideo(onClickPlayButton: () -> Unit) {
    onClickPlayButton()
}

/**
 * Non-local jump expressions
 * Mot lambda khi inline no duoc chen truc tiep vao code nen co the return duoc
 * Khong return ham bao ngoai duoc khi goi return trong lambda (object) gay loi cho ham ch
 */

inline fun test2(number: Int, onReturn: () -> Boolean) {
    if (number % 2 == 0) {
        onReturn()
    }
}

fun test1(number: Int, onReturn: () -> Boolean) {
    if (number % 2 == 0) {
        onReturn()
    }
}

fun testNonLocalJumpExpress(): Boolean {
//    test1(2){
//        return false
//    }
    test2(2) {
        return true
    }
    println("Ham inline chua return")
    return true
}

fun main() {

    val items = listOf(1, 2, 3, 4, 5)
    val result = demoHigherFunction(items, result = { a, b -> a * b })

    /**
     * Function references for higher-order function
     */
    val result2 = demoHigherFunction(items, ::pow)
    val enableButton: Boolean = (result % 2) == 0
    clickButton(enableButton, onClickPlayButton = {
        println("Playing")
    })
    val fruits = listOf("Apple", "Banana", "Orange")
    fruits.printlnFruits(relationship = { fruit, relation -> "$fruit $relation" })
    /**
     * Lambda express
     */
    val s = { a: Int, b: Int -> a + b }
    println(s(1, 2))

    /**
     * Reference function
     */
    val helloName: (String) -> String = ::helloMessage
    println(helloName("Phuc_cdr"))

    /**
     * Inline function
     */
    playVideo(onClickPlayButton = {
        println("Playing Video")
    })

    /**
     * Function type reiver
     */
    val hello: String.(String) -> String = { other ->
        "$this $other"
    }
    println("Xin chao".hello("Phuc :)"))

    val print: suspend (a: String) -> Unit = {
        println("okela $it")
    }

    runBlocking {
        print("akelo")
    }

    val categories: CategoryMap = hashMapOf()
    categories["Life"] = "uri"
    val helloMessage = createHelloMessage(helloName)

    /**
     * non local return
     */
    println(testNonLocalJumpExpress())

}