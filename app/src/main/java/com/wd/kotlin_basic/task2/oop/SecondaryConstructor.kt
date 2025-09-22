package com.wd.kotlin_basic.task2.oop

/** Secondary constructors
 * override function and properties
 * **/
 class Laptop(
    private var owner: Person = Person(),
    override var name: String,
    private var ram: Int
) : Equipment() {
    override var manufacturer: String = "Dell"

    init {
        owner.laptops.add(this)
    }

    override fun guarantee() {
        println("2 year guarantee")
    }

    override fun toString(): String {
        return "Laptop(owner = ${owner}, name='$name', ram=$ram)"
    }
}

fun main(){
    val phuc = Person("Phuc", "Thai Huu", true)
    println(phuc)
    println(phuc.isAndroidDeveloper)
    val laptop = Laptop(phuc, "Dell", 24)
    println(laptop)
}

