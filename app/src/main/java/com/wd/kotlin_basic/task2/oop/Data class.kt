package com.wd.kotlin_basic.task2.oop

/** Data class **/
data class File(val name: String, val size: Int, val path: String) {
    override fun toString(): String {
        return "File(name='$name', size=$size, path='$path')"
    }

    override fun hashCode(): Int {
        return name.hashCode() + size.hashCode() + path.hashCode() + 3
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (name != other.name) return false
        if (size != other.size) return false
        if (path != other.path) return false

        return true
    }
}

fun main() {
    val file = File("Mua do", 5, "D:/")
    println(file)
}