package com.wd.kotlin_basic.task1

/** 2. Cấu trúc điều kiện và vòng lặp **/


/** if else **/
fun checkPassword(password: String, confirmPassword: String): Boolean {
    if (!password.equals(confirmPassword) || password.length < 8){ return false}
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
    if (a <= 0) {
        return null
    }
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