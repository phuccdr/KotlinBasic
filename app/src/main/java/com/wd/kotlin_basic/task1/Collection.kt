package com.wd.kotlin_basic.task1

/** 3.Collection (Danh sách dữ liệu)
 * Collection<T> is the root of the collection hierarchy.
 * **/

fun syntaxForCollections(a: Collection<Int>, b: Collection<Int>) {
    println(a.union(b))
    println(a.intersect(b))
    println(a.subtract(b))
    println(a.contains(4))
    println(a.containsAll(b))
    println(a.isEmpty())
    println(a.size)
    println(a.iterator())

    val iterator = a.iterator()
    while (iterator.hasNext()) {
        println(iterator.next())
    }
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