package com.wd.kotlin_basic

import kotlin.random.Random

private const val TAG = "Kotlin_Basic"
private val internAndroidDevelopers: List<InternAndroidDeveloper> = listOf(
    InternAndroidDeveloper(
        "Thai Huu Phuc",
        2004,
        "Học viện công nghệ bưu chính viễn thông",
        WorkMode.HYBRID
    ),
    InternAndroidDeveloper("Nguyen Van A", 2000, "Đại học Bách Khoa Hà Nội", WorkMode.ONLINE),
    InternAndroidDeveloper("Tran Thi B", 2001, "Đại học Công nghệ - ĐHQG Hà Nội", WorkMode.OFFLINE),
    InternAndroidDeveloper("Le Van C", 1999, "Đại học FPT", WorkMode.HYBRID),
    InternAndroidDeveloper("Pham Thi D", 2002, "Đại học Kinh tế Quốc dân", WorkMode.ONLINE),
    InternAndroidDeveloper("Hoang Van E", 2000, "Đại học Bách Khoa TP.HCM", WorkMode.OFFLINE),
    InternAndroidDeveloper("Do Thi F", 2001, "Đại học Sư phạm Kỹ thuật TP.HCM", WorkMode.OFFLINE),
    InternAndroidDeveloper("Vo Van G", 1998, "Đại học Công nghiệp Hà Nội", WorkMode.ONLINE),
    InternAndroidDeveloper(
        "Dang Thi H",
        2002,
        "Đại học Khoa học Tự nhiên - ĐHQG TP.HCM",
        WorkMode.OFFLINE
    ),
    InternAndroidDeveloper("Nguyen Van I", 1999, "Đại học Thủy lợi", WorkMode.OFFLINE),
    InternAndroidDeveloper("Tran Thi K", 2000, "Đại học Giao thông Vận tải", WorkMode.ONLINE)
)

fun selectInternAndroidDeveloper(): InternAndroidDeveloper {
    val index = Random.nextInt(0, 11)
    return internAndroidDevelopers[index]
}


fun filterInternsByWorkMode(workMode: WorkMode): List<InternAndroidDeveloper> {
    val filteredInterns = internAndroidDevelopers.filter { it.workMode == workMode }
    println("There are ${filteredInterns.size} interns working $workMode:")
    filteredInterns.forEach { intern ->
        println("${intern.name} - ${intern.university}")
    }
    return filteredInterns
}

fun calculateAverageAge(): Double {
    val totalAge = internAndroidDevelopers.map { 2025 - it.yearOfBirth }
        .reduce { acc, age -> acc + age }
    val averageAge = totalAge.toDouble() / internAndroidDevelopers.size
    println("Total age: $totalAge")
    println("Average age: ${String.format("%.2f", averageAge)}")
    return averageAge
}

fun findYoungestAndOldest() {
    val sortedByAge = internAndroidDevelopers.sortedBy { it.yearOfBirth }

    val oldest = sortedByAge.first()
    val youngest = sortedByAge.last()

    val oldestAge = 2025 - oldest.yearOfBirth
    val youngestAge = 2025 - youngest.yearOfBirth

    println(" ${oldest.name} - ($oldestAge)")
    println(" ${youngest.name} - ($youngestAge)")
}

//fun main() {
//    var myName:String = readLine().toString().trim()
//    while(myName.isEmpty()){
//        println("Please enter your name")
//        myName = readLine().toString().trim()
//    }
//    val fresherAndroidDeveloper = selectInternAndroidDeveloper()
//    if(fresherAndroidDeveloper.name == myName){
//        println("Congratulations $myName")
//    }
//    println("Fresher Android Developer: $fresherAndroidDeveloper")
//    filterInternsByWorkMode(WorkMode.HYBRID)
//    calculateAverageAge()
//    findYoungestAndOldest()
//}

fun main1() {
    var a = 1
// simple name in template:
    val s1 = "a is $a"
    println(s1)
    a = 2
// arbitrary expression in template:
    val s2 = "${s1.replace("is", "was")}, but now is $a"
    println(s2)

    for (index in internAndroidDevelopers.indices) {
        println(" ${index} ${internAndroidDevelopers[index]}")
    }

    val x = 10
    val y = 9
    if (x in 2..y + 1) {
        println("fits in range")
    }

    for (i in 1..10 step 2) {
        println(i)
    }
    for (i in 10 downTo 0 step 2) {
        println(i)
    }

    val fruits: List<String> = listOf("banana", "avocado", "apple", "kiwifruit", "orange")
    val myFruitIndex = Random.nextInt(fruits.size)
    when (fruits[myFruitIndex]) {
        "banana" -> println("banana is good")
        "avocado" -> println("avocado is good")
        "apple" -> println("apple is good")
        "kiwifruit" -> println("kiwifruit is good")
        "orange" -> println("orange is good")
    }
    fruits
        .filter { it.startsWith("a") }
        .sortedBy { it }
        .map { it.uppercase() }
        .forEach { println(it) }

    val name = "Eco mobile"
    println(name.splitString())

}


fun union(a: List<Int>, b: List<Int>) {
    val set1 = a.toSet()
    val set2: MutableSet<Int> = b.toMutableSet()
    set2.add(10)
    val union = set1.union(set2)
    println(union)
}

fun frequencyCounter(a: List<Int>) {
    val map = mutableMapOf<Int, Int>()
    a.forEach {
        map[it] = (map[it] ?: 0) + 1
    }
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

fun MutableList<Int>.swap(index1: Int, index2: Int) {
    if (index1 !in this.indices || index2 !in this.indices) {
        println("Invalid index")
        return
    }
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

// Generic function
fun <T> add(a: T, b: T): String {
    return a.toString() + b.toString()
}

fun main() {
    val list = mutableListOf(1, 2, 3, 4, 2, 3, 2, 4, 1, 3, 2, 3)
    list.swap(0, 6)
    println(list.reversed())
    frequencyCounter(list)

    val a: MutableMap<String, Int> = mutableMapOf("Phuc" to 3, "Huu" to 2, "Thai" to 1)
    println(a.toSortedMap())
    var b: MutableMap<Int, String> = mutableMapOf()
    a.forEach { (key, value) ->
        b[value] = key
    }
    b = b.toSortedMap()
    for ((key, value) in b) {
        print(b[key] + " ")
    }
    println()
    println(add(1, 2))

    val pair = Pair("Phuc",209)
    println(pair)
}

class Pair<K, V>(val first: K, val second: V) {
    override fun toString(): String {
        return "($first,$second)"
    }
}


