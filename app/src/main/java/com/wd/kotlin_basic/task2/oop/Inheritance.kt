package com.wd.kotlin_basic.task2.oop


/** Interface **/
interface tryHard {
    fun tryHard() {
        println("Try your best")
    }
}

/** Open class **/
open class Employee(protected val name: String, protected val department: String) : tryHard {
    open fun work() {
        println("Employee is working")
    }
}

class AndroidDeveloper(name: String, department: String, skills: List<String>) :
    Employee(name, department) {
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
    val androidDeveloper = AndroidDeveloper(
        "Phuc", "R&D", mutableListOf("Kotlin", "Java", "C++")
    )
    androidDeveloper.work()
    androidDeveloper.showSkills()
    androidDeveloper.addSkill("Coroutine")
    androidDeveloper.showSkills()
    androidDeveloper.tryHard()
}