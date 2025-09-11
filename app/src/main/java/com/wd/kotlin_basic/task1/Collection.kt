package com.wd.kotlin_basic.task1

import java.util.Collections

/** 3.Collection (Danh sách dữ liệu)
 * Collection<T> is the root of the collection hierarchy.
 * Collection's inheritors: List and Set.
 * **/

/** Cac ham quan trong trong Collections **/

fun sumList(a: Set<Int>): Int {
    return a.reduce { acc, i -> acc + i }
}

/** filter, map, sortedBy, groupBy **/

fun chooseFruits(fruits: List<String>): List<String> {
    return fruits.filter { it.first().equals('a') }
        .map { it.uppercase() }
        .sortedBy { it }
}


fun syntaxForCollections(a: Collection<Int>, b: Collection<Int>) {
    println(a.union(b))
    println(a.intersect(b))
    println(a.subtract(b))
    println(a.contains(4))
    println(a.containsAll(b))
    println(a.isEmpty())
    println(a.size)
    println(a.iterator())
    println(a.reversed())
    println(a.sorted())
    println(a.sortedDescending())
    println(a.sortedBy { it * it })
    println(a.groupBy { it % 100 })

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

/** HashSet
 * An alternative implementation – HashSet – says nothing about the elements order, so calling such functions on it returns unpredictable results. However, HashSet requires less memory to store the same number of elements.
 */

fun syntaxForHashSet() {
    val hashSet: HashSet<Int> = hashSetOf(1, 2, 3, 4, 5)
    println(hashSet.contains(3))
    println(hashSet.indexOf(4))
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

fun groupByCaloFruits(fruits: Map<String, Int>): Map<Int, List<String>> {
    val result: MutableMap<Int, MutableList<String>> = mutableMapOf()
    fruits.forEach { (key, value) ->
        if (result.containsKey(value)) {
            result[value]?.add(key)
        } else {
            result[value] = mutableListOf(key)
        }
    }
    return result
}


fun main() {
    syntaxForHashSet()
    val collection: MutableCollection<String> = mutableListOf("Thai", "Phuc", "Huu")
    val a: MutableSet<Int> = mutableSetOf(1, 2, 3, 4)
    val set = setOf(1,2,4,3,3)
    println(set)
    val iterator = set.iterator()
    println(iterator.next())
    collection.iterator()

}