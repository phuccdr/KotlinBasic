package com.wd.kotlin_basic.task2.oop

/** ObjectOrientedProgramming **/
fun main() {
    /** Class **/
    val phuc = Person("Phuc", "Thai Huu", true)
    println(phuc)
    println(phuc.isAndroidDeveloper)
    val laptop = Laptop(phuc, "Dell", 24)
    println(laptop)
    println(phuc.laptops)
    phuc.laptops.first().guarantee()
    val file = File("Mua do", 5, "D:/")
    println(file)
    /** Object declaration **/
    val cacheData = CacheData.data.add("Java")
    println(cacheData)
    println(CacheData.checkContain("Kotlin"))
    /** Companion object **/
    val circle = Circle.create(5.0)
    println(circle.radius)

}