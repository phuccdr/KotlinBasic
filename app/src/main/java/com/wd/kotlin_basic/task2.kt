package com.wd.kotlin_basic

import java.util.UUID

/**Functions **/

fun diff(a: Int, b: Int): Int {
    return a - b
}

/**Single-expression functions **/
fun sum(a: Int, b: Int) = a + b

/**Default Argument Values and Named Arguments **/
fun createHelloKotlinMessage(name: String = "Kotlin"): String {
    return "Hello $name"
}

/** Extension function **/
fun String.splitString(): List<String> {
    return this.split(" ")
}

/**Null Safety**/
fun createHelloMessage(name: String?): String? {
    if (name.isNullOrEmpty()) return null
    return "Hello $name"
}

fun createHelloMessageSafeCall(name: String?): String {
    return createHelloMessage(name) ?: "Hello Kotlin"
}

fun showHelloMessage(name: String?, companyName: String?) {
    try {
        if (name.isNullOrEmpty()) {
            println(" your name is empty")
        }
        val helloMessage = createHelloMessageSafeCall(name) + " " + companyName!!
        println(helloMessage)
    } catch (e: Exception) {
        println(e.message)
    }
}

fun main() {
    showHelloMessage(null, "Eco Mobile")
    val phuc = Person("Phuc", "Thai Huu", true)
    println(phuc)
    println(phuc.isAndroidDeveloper)

    val laptop = Laptop(phuc, "Dell", 24)
    println(laptop)
    println(phuc.laptops)

}

class Person(
    val firstName: String = "firstName",
    val lastName: String = "lastName",
    val isEmployeed: Boolean = false,
    val laptops: MutableList<Laptop> = mutableListOf()
) {

    val isAndroidDeveloper by lazy {
        studyKotlin()
        true
    }

    private fun studyKotlin() {
        println("what is kotlin?")
    }

    override fun toString(): String {
        return "Person(firstName='$firstName', lastName='$lastName', isEmployeed=$isEmployeed)"
    }
}

class Laptop {
    private var owner: Person = Person()
    private var brand: String = "Dell"
    private var ram: Int = 16

    constructor(owner: Person, brand: String, ram: Int) {
        owner.laptops.add(this)
        this.brand = brand
        this.ram = ram
        this.owner = owner
    }

    override fun toString(): String {
        return "Laptop(owner = ${owner}, brand='$brand', ram=$ram)"
    }
}


open class Employee() {
    private val id = UUID.randomUUID().toString()
    private val name: String? = null
    private val department: String? = null

    open fun work(){
        println("Employee is working")
    }
}

class AndroidDeveloper():Employee(){
    override fun work() {
        super.work()
        println("Android Developer is developing an app")
    }
}












