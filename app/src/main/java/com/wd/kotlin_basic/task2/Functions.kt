package com.wd.kotlin_basic.task2

/**Functions **/

/**Single-expression functions **/
fun sum(a: Int, b: Int) = a + b

/**Default Argument Values and Named Arguments **/
fun createHelloKotlinMessage(name: String = "Phuc :)"): String {
    return "Hello $name"
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

fun String.normalizeName(name: String): String {
    return name.trim().lowercase().split(" ").map {
        it.substring(0, 1).uppercase() + it.substring(1)
    }.joinToString(separator = " ")
}

/** Generic function **/
fun <T> compare(a: T, b: T): Boolean {
    return a == b
}

/** Unit parameter **/
fun research(topic: String, code: () -> Unit) {
    println(" researching $topic")
    code.invoke()
}

fun code() {
    research("kotlin") {
        println("coding")
    }
}

/** Variable number of arguments (varargs)
 * Inside a function, a vararg-parameter of type T is visible as an array of T
 * **/
fun <T> clickItem(vararg categories: T) {
    for (i in categories) {
        println(i)
    }
}

/** Function scope **/

/** Let:
 * Executing a lambda on non-nullable objects and
 * Introducing an expression as a variable in local scope
 * **/
fun loadState(state: String?) {
    state?.let {
        println("state already loaded")
    }
}

/** Run: Object configuration and computing the result **/
fun changeUserName(newName: String) {
    newName.run {
        val name = normalizeName(name = this)
        println("new name is $name")
    }
    println(newName)
}

class Category(var name: String, var topic: String, var color: Int, var counterClick: Int = 0) {
    fun click() {
        counterClick++
    }

}

/** Apply: Object configuration **/
fun changeCategory(category: Category) {
    category.apply {
        topic = "Travel"
        color = 0xffff00
    }
}

/** With Grouping function calls on an object **/
fun changeCategory2(category: Category) {
    with(category) {
        click()
        click()
        click()
    }
}


fun main() {
    val category = Category("Life", "Family", 0xff0010)
    changeCategory(category)
    changeCategory2(category)
    println(category)
    code()
    clickItem("hihi", "Hehe", "HEHE")
    changeUserName("thai HuU pHuc")
}


