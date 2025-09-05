package com.wd.kotlin_basic.kotlin_exercises

data class InternAndroidDeveloper(
    val name: String,
    val yearOfBirth: Int,
    val university: String,
    val workMode: WorkMode
)

enum class WorkMode {
    ONLINE,
    OFFLINE,
    HYBRID
}
