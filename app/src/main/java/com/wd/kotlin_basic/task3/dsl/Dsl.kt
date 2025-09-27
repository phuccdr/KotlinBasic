package com.wd.kotlin_basic.task3.dsl

/**
 * DSL (domain-specific language) là gi ?
 * DSL la 1 cach viet code dac biet giup mo ta logic ngan gon de doc, gan voi ngon ngu tu nhien.
 * Trong kotlin no co cach tinh nang:
 * +) Lambdas
 * +) Higher-order functions
 * +) Extension functions
 * +) Scope functions
 * +) Builder design pattern
 * +) Infix notation
 * +) Operator overloading
 * +) Inline function
 * +) Singleton design pattern/ anti_pattern
 */
/**
 * Extension function
 */
fun String.normalizeUserName(): String {
    return this.trim().split(" ").filter { it.isNotBlank() }.joinToString(separator = " ") {
        it.lowercase().replaceFirstChar { char ->
            char.uppercase()
        }
    }
}

/**
 * ScopeFunction
 */

data class Person(var name:String,val age:Int)
fun greetMessage(name: String?) {
    //let ktra nullable
    name?.let{
        println("Hello ${it.normalizeUserName()}")
    }
    //run: thao tac nhieu lan tren object va cuoi cung tra ra kq
    val message1 =name?.run {
       "Hello ${normalizeUserName()}"
    }
    println(message1)
    println(name)


    //with: goi nhieu phuong thuc tren object
    val message2 = with(name ?: " phuc"){
        normalizeUserName()
        uppercase()
    }
    println(message2)
    println(name)

    //apply: khoi tao, thiet lap thuoc tinh object ma van giu lai object do.
    val mit = Person("mit  cDr",21)
    mit.apply {
     this.name = this.name.normalizeUserName()
    }
    println(mit)
}

/**
 * Builder design pattern
 */



fun main(){
    greetMessage("THAI huU  Phuc  ")
}




