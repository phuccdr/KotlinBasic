package com.wd.kotlin_basic.task3.kotlinreflection

/**
 * Class references
 */
data class Laptop(
    val name: String,
    val brand: String,
    val ram: Int,
    val rom: Int,
    val cpu: String,
    val screenSize: Double
)

/**
 * Function references
 */
fun normalizationName(name: String): String {
    return name.split(" ").filter { it.isNotBlank() }
        .joinToString(separator = " ") { it.lowercase().replaceFirstChar { it.uppercase() } }
}

fun greet(name: String, handelName: (String) -> String, major: String) {
    println("Mr.${handelName(name)}: $major")
}

fun main() {
    val laptop = Laptop("MacBook Pro", "Apple", 16, 512, "CPU", 24.0)
    println(laptop::name.get())
    println(laptop::toString.invoke())

    val function: (String) -> String = ::normalizationName
    val name = "Thai    hUU  phuC"
    println(function(name))

    greet(name, ::normalizationName, "Dev")
}