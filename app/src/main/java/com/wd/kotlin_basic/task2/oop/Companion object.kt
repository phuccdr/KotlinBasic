package com.wd.kotlin_basic.task2.oop

/** Companion object **/
class Circle(val radius: Double) {
    companion object {
        fun create(radius: Double): Circle {
            return Circle(radius)
        }
    }
}

fun main() {
    val circle = Circle.create(5.0)
    println(circle.radius)
}