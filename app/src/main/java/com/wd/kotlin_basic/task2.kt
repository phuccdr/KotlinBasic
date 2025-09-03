package com.wd.kotlin_basic

import java.util.UUID

/**Functions **/
fun diff(a: Int, b: Int): Int {
    return a - b
}

/** Extension function **/
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    if (index1 !in this.indices || index2 !in this.indices) {
        println("Invalid index")
        return
    }
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun String.normalizeName2(name: String): String {
    return name.trim().lowercase().split(" ").map {
        it.substring(0, 1).uppercase() + it.substring(1)
    }.joinToString(separator = " ")
}

/** Generic function **/
fun <T> compare(a: T, b: T): Boolean {
    return a == b
}

/**Single-expression functions **/
fun sum(a: Int, b: Int) = a + b

/**Default Argument Values and Named Arguments **/
fun createHelloKotlinMessage(name: String = "Phuc :)"): String {
    return "Hello $name"
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

fun safetyNormalizeName(name: String?): String {
    var result: String? = null
    name?.let {
        result = normalizeName(name)
    }
    return result ?: "Phuc Thai Van"
}

/**OOP **/

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

abstract class Equipment {
    abstract var name: String
    abstract var manufacturer: String

    abstract fun guarantee()
}

class Laptop : Equipment {
    private var owner: Person = Person()
    override var name: String = "Vostro:))"
    override var manufacturer: String = "Dell"
    private var ram: Int = 16

    constructor(owner: Person = Person(), name: String, ram: Int) {
        owner.laptops.add(this)
        this.name = name
        this.ram = ram
        this.owner = owner
    }

    override fun guarantee() {
        println("2 year guarantee")
    }

    override fun toString(): String {
        return "Laptop(owner = ${owner}, name='$name', ram=$ram)"
    }
}

interface tryHard {
    fun tryHard() {
        println("Try your best")
    }
}

open class Employee() : tryHard {
    private val id = UUID.randomUUID().toString()
    private val name: String? = null
    private val department: String? = null

    open fun work() {
        println("Employee is working")
    }
}

class AndroidDeveloper() : Employee() {
    private val skills: MutableList<String> = mutableListOf()
    override fun work() {
        super.work()
        println("Android Developer is developing an app")
    }

    fun showSkills() {
        println("Skills: $skills")
    }

    fun addSkill(skill: String) {
        skills.add(skill)
    }

    override fun tryHard() {
        super.tryHard()
        println("Android Developer is trying hard")
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
    val androidDeveloper = AndroidDeveloper()
    androidDeveloper.addSkill("Kotlin")
    androidDeveloper.showSkills()
    androidDeveloper.work()
    androidDeveloper.tryHard()
    phuc.laptops.first().guarantee()
}






