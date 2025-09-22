package com.wd.kotlin_basic.task2.oop

/** Object expressions **/
interface Drag {
    fun dragging()
}

fun main() {
    /** Object expression **/
    val finger = object : Drag {
        override fun dragging() {
            println("Dragging")
        }
    }
    finger.dragging()
}