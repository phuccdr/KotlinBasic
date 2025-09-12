package com.wd.kotlin_basic.task1

fun main() {
    val set = hashSetOf(1, 3, 2, 4)

    for (i in 0..5500) {
        set.add(i)
    }

   val s1= set.toString()
    set.add(2222222)
    val s2 =set.toString()
    println(s1)
    println(s2)

}