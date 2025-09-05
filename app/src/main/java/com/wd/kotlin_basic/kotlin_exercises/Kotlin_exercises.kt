package com.wd.kotlin_basic.kotlin_exercises

fun translate(rna: String?): List<String> ?{
    require(!rna.isNullOrBlank() && rna.all { it.isLetter() })
    val stop = listOf("UAA", "UAG", "UGA")
    var list = rna.chunked(3)
    var indexStop = list.indexOfFirst { stop.contains(it) }
    if (indexStop == -1) indexStop = list.size
    list = list.subList(0, indexStop)
    val result: List<String>? = list.map { codon ->
        when (codon) {
            "AUG" -> "Methionine"
            "UUU", "UUC" -> "Phenylalanine"
            "UUA", "UUG" -> "Leucine"
            "UCU", "UCC", "UCA", "UCG" -> "Serine"
            "UAU", "UAC" -> "Tyrosine"
            "UGU", "UGC" -> "Cysteine"
            "UGG" -> "Tryptophan"
            else -> throw IllegalArgumentException("Invalid codon")
        }
    }
    if(list.isEmpty()) return null
    return result
}

fun transcribeToRna(dna: String): String = dna.map{ nu ->
    when(nu){
        'G'->'C'
        'C'->'G'
        'T'->'A'
        'A'->'U'
        else -> throw IllegalArgumentException("Invalid nucleotide")
    }
}.joinToString("")


object SumOfMultiples {

    fun sum(factors: Set<Int>, limit: Int): Int {
        val set:MutableSet<Int> = mutableSetOf()
        factors.forEach {
            var tmp =it
            if(it==0){
               tmp = limit+1
            }
            while(tmp<limit){
                set.add(tmp)
                tmp+=it
            }
        }
        return set.sum()
    }
}

class Triangle<out T : Number>(val a: Number, val b: Number, val c: Number) {

  init{
      require(a.toDouble()>0&&b.toDouble()>0&&c.toDouble()>0)
        require(a.toDouble()+b.toDouble()>=c.toDouble()&&b.toDouble()+c.toDouble()>=a.toDouble()&&a.toDouble()+c.toDouble()>=b.toDouble())
    }
    val isEquilateral: Boolean = a==b&&b==c&&c==a
    val isIsosceles: Boolean = a==b||b==c||c==a
    val isScalene: Boolean = !isEquilateral &&!isIsosceles
}


fun twofer(name: String ="you"): String = "One for $name, one for me."



fun main() {
    println(twofer("Phuc"))
}

