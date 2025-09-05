package com.wd.kotlin_basic.task2

import java.util.UUID

/** ObjectOrientedProgramming **/

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

/** Secondary constructors **/
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


/** interface **/
interface tryHard {
    fun tryHard() {
        println("Try your best")
    }
}

/** parent class **/
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