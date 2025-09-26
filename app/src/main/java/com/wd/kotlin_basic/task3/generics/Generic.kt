package com.wd.kotlin_basic.task3.generics

fun <T> List<T>.compareCustom(other: List<T>): Boolean {
    val set1 = this.toSet()
    val set2 = other.toSet()
    return set1 == set2
}

fun <T> MutableCollection<T>.addNewCollection(col: Collection<T>): Collection<T> {
    return this.apply {
        addAll(col)
    }
}

fun main() {
    val list1 = listOf(1, 2, 3, 4, 5)
    val list2 = listOf(4, 2, 6, 4, 9, 10, 3, 1, 5)
    println(list1.compareCustom(list2))
    val map1: MutableSet<String> = mutableSetOf("mot", "hai", "ba", "bon", "nam")
    val map2: Set<String> = setOf("sau", "bay", "tam", "chin", "hehe")
    println(map1.addNewCollection(map2))
}