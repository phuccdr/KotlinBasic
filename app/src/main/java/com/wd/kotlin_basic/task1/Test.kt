package com.wd.kotlin_basic.task1

import android.util.Log

fun hexToDecimal(hex: String): Long {
    return hex.toLong(16) // 16 là cơ số hệ hex
}
//fun main() {
//    val set = hashSetOf(1, 3, 2, 4)
//
//    for (i in 0..5500) {
//        set.add(i)
//    }
//
//   val s1= set.toString()
//    set.add(2222222)
//    val s2 =set.toString()
//    println(s1)
//    println(s2)
//    val collection :Collection<Int> = listOf(0,1,2,3,4)
//
//    val a:Int = 2
//    println(a.hashCode())
//    val set1:HashSet<Int> = hashSetOf(1,2,3,4,5)
//    println("Set1:")
//    println(set1.hashCode()) // hashCode cua 1 Set bang tong cac hash code cua cac phan tu con.
//    println(set1.iterator())
//    println(set1.iterator().hashCode())
//    println(set1.contains(a))
//    val set2:HashSet<Int> = hashSetOf(0,1,2,6,4,5)
//    println(hexToDecimal("3c756e4d"))
//}

//fun main() {
//    val set: Set<Int> = setOf(1, 2, 3, 4, 5)
//    val contain = set.contains(2)
//    val set2: LinkedHashSet<Int> = linkedSetOf(1, 2, 3, 4, 5)
//    val hashSet: HashSet<Int> = hashSetOf(1, 2, 3, 6, 4, 5)
//    hashSet.add(68)
//    val iterator = hashSet.iterator()
//    while (iterator.hasNext()) {
//        println(iterator.next())
//    }
//
//    val hashMap: HashMap<Int, String> = hashMapOf(1 to "a", 2 to "b", 3 to "c")
//    println(hashMap.containsKey(1))
//    hashMap.put(4, "d")
//    var list1 = listOf<Int>(1,2,3,4)
//    var list2:MutableList<Int> = list1 as MutableList<Int>
//    if (list2 is MutableList) println("true")
//    println(list1.hashCode())
//    println(list2.hashCode())
//    list1.add(5)
//    println(list2.toString()
//
//
//}

fun main(){
    var list:List<Int> = listOf(1,2,3,4,5)
    val a = buildList {
        add(1)
        add(2)
        listOf(3)
        this.removeAt(0)
        add(3)
        list =this
    }
    println(a.hashCode())
    println(list.hashCode())
    println(list)

    val linkedHashSet:LinkedHashSet<Int> = linkedSetOf(1,2,3,4,5)
    val hashSet:HashSet<Int> = hashSetOf(1,2,3,4,5)
    val mutableSet = mutableSetOf(1,2,3,4,5)
    val set = setOf(1,2,3,4,5)
    val a1=2
    val a2=3
    val list1 =a
    println(list===list1)

    val Laptop1 = LapTop("Dell", 1500)
    val Laptop2 = LapTop("Dell-Vostro", 1500)
    println(Laptop1.equals(Laptop2))
    println(Laptop1==Laptop2)
    println(Laptop1.hashCode())
    println(Laptop2.hashCode())

    val set1 = setOf(Laptop1,Laptop2)

}

class LapTop(val name:String, val price:Int){
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as LapTop
//
//        if (name != other.name) return false
//        if (price != other.price) return false
//
//        return true
//    }

//    override fun hashCode(): Int {
//        super.hashCode()
//        var result = name.hashCode()
//        result = 31 * result + price
//        return result
//    }

}