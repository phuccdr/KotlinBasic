package com.wd.kotlin_basic.task2.oop

/** Class
 * lazy initialization
 * **/
class Person(
    private val firstName: String = "firstName",
    private val lastName: String = "lastName",
    private val isEmployed: Boolean = false,
    laptops: List<Laptop> = listOf()
) {
    val laptops = laptops.toMutableList()

    val isAndroidDeveloper: Boolean by lazy {
        studyKotlin()
        true
    }

    private fun studyKotlin() {
        println("what is kotlin?")
    }

    override fun toString(): String {
        return "Person(firstName='$firstName', lastName='$lastName', isEmployeed=$isEmployed), isAndroidDeveloper=$isAndroidDeveloper)"
    }
}

fun main() {
    val person = Person("Phuc", "Thai Huu", false, listOf(Laptop(Person(), "Dell", 24)))
    println(person.isAndroidDeveloper)
    println(person)
}