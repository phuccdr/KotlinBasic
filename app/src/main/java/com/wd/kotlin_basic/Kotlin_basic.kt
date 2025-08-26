package com.wd.kotlin_basic

import kotlin.random.Random

private const val TAG = "Kotlin_Basic"
private val internAndroidDevelopers: List<InternAndroidDeveloper> = listOf(
    InternAndroidDeveloper("Thai Huu Phuc", 2004, "Học viện công nghệ bưu chính viễn thông", WorkMode.HYBRID),
    InternAndroidDeveloper("Nguyen Van A", 2000, "Đại học Bách Khoa Hà Nội",WorkMode.ONLINE),
    InternAndroidDeveloper("Tran Thi B", 2001, "Đại học Công nghệ - ĐHQG Hà Nội",WorkMode.OFFLINE),
    InternAndroidDeveloper("Le Van C", 1999, "Đại học FPT",WorkMode.HYBRID),
    InternAndroidDeveloper("Pham Thi D", 2002, "Đại học Kinh tế Quốc dân",WorkMode.ONLINE),
    InternAndroidDeveloper("Hoang Van E", 2000, "Đại học Bách Khoa TP.HCM",WorkMode.OFFLINE),
    InternAndroidDeveloper("Do Thi F", 2001, "Đại học Sư phạm Kỹ thuật TP.HCM",WorkMode.OFFLINE),
    InternAndroidDeveloper("Vo Van G", 1998, "Đại học Công nghiệp Hà Nội",WorkMode.ONLINE),
    InternAndroidDeveloper("Dang Thi H", 2002, "Đại học Khoa học Tự nhiên - ĐHQG TP.HCM",WorkMode.OFFLINE),
    InternAndroidDeveloper("Nguyen Van I", 1999, "Đại học Thủy lợi",WorkMode.OFFLINE),
    InternAndroidDeveloper("Tran Thi K", 2000, "Đại học Giao thông Vận tải",WorkMode.ONLINE)
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
        .reduce {acc, age -> acc +age}
    val averageAge = totalAge.toDouble() / internAndroidDevelopers.size
    
    println("Total age: $totalAge")
    println("Average age: ${String.format("%.2f",averageAge)}")
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

fun main() {
    var myName:String = readLine().toString().trim()
    while(myName.isEmpty()){
        println("Please enter your name")
        myName = readLine().toString().trim()
    }
    val fresherAndroidDeveloper = selectInternAndroidDeveloper()
    if(fresherAndroidDeveloper.name == myName){
        println("Congratulations $myName")
    }
    println("Fresher Android Developer: $fresherAndroidDeveloper")
    filterInternsByWorkMode(WorkMode.HYBRID)
    calculateAverageAge()
    findYoungestAndOldest()
}

