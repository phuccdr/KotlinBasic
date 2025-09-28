package com.wd.kotlin_basic.task3.dsl

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton


/**
 * DSL (domain-specific language) là gi ? Thuong dung trong custom view
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
 * vi du dung DSL de customView
 */

class CustomButton @JvmOverloads constructor(
    context:Context,
    attrs:AttributeSet?=null,
    defStyleAttr:Int = android.R.attr.buttonStyle
):AppCompatButton(context,attrs,defStyleAttr){
    var cornerRadius:Float = 20f
        set(value){
            field = value
            invalidate()
        }
    var bgColor:Int = Color.parseColor("#000000")
        set(value){
            field = value
            updateBackground()
        }


    private fun updateBackground(){
        val shape = GradientDrawable().apply{
            cornerRadius = this@CustomButton.cornerRadius
            setColor(this@CustomButton.bgColor)
        }
        background = shape
    }
}

class CustomButtonBuilder(private val context:Context){
    var text:String = "Button default"
    val textSize :Float = 16f
    var bgColor:Int = Color.parseColor("#000000")
    var cornerRadius: Float = 20f
    private var onClick:(()->Unit)?=null

    fun onClick(listener:()->Unit){
        onClick = listener
    }

    fun setBackGroundColor(color:Int){
        bgColor = color
    }

    fun build():CustomButton = CustomButton(context).apply{
        text = this@CustomButtonBuilder.text
        textSize = this@CustomButtonBuilder.textSize
        bgColor = this@CustomButtonBuilder.bgColor
        cornerRadius = this@CustomButtonBuilder.cornerRadius
        setOnClickListener { onClick?.invoke() }


        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 16, 16, 16)
        }
    }

}

fun Context.customButton(block: CustomButtonBuilder.() -> Unit): CustomButton {
    return CustomButtonBuilder(this).apply(block).build()
}



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
 * Builder design pattern:
 * Nham tranh viec nham lan khi tạo 1 object co nhieu thuoc tinh cung kieu.
 * Tao constructor co cac gia tri mac dinh va tao ham build de set cac thuoc tinh (dung apply)
 */

data class Breakfast(
    val bread: Boolean,
    val butter: Boolean,
    val juice: String,
    val mainDish: String
)

class BreakfastBuilder {
    var bread: Boolean = false
    var butter: Boolean = false
    var juice: String = ""
    var mainDish: String = ""

    fun build() = Breakfast(bread, butter, juice, mainDish)

    fun withBread(bread:Boolean): BreakfastBuilder {
        this.bread = bread
        return this
    }
}


/**
 * Infix notation: dung ham khong can dau cham va dau ()
 */

infix fun Int.add(number:Int):Int{
    return this + number
}

infix fun String.add(string:String):String{
    return "$this $string"
}

/**
 * Operator overloading: dinh nghia cac toan tu tren doi tuong
 */
data class Point(val x:Int,val y:Int){
    operator fun plus(point: Point):Point{
        return Point(x+point.x,y+point.y)
    }
}

/**
 * Inline function: ham inline chen code truc tiep vao code ham bao ngoai
 */

inline fun onButtonClick(onClick:()->Unit, isEnable:Boolean){
    if(isEnable){
        onClick()
    }else{
        println("Button is disable")
    }
}

/**
 * Singleton design pattern/antipattern: Dung object de tao ra 1 doi tuong duy nhat trong toan bo app.
 * Tranh lam dung dùng singleton gay ton bo nho.
 */

object Database{
    fun connect(){
        println("Connected database")
    }
}


fun main(){
   

    greetMessage("THAI huU  Phuc  ")

    val breakfast:Breakfast = BreakfastBuilder().apply {
        bread = true
        butter = true
        juice = "Orange juice"
        mainDish = "Eggs"
    }.build()

    val breakfast2 = BreakfastBuilder().withBread(true).build()
    println(breakfast)
    println(breakfast2)

    val a=2
    println(a add 2)
    val string1 = "Toi"
    val string2 = "la toi"
    println(string1 add string2)

    val point1 = Point(1,2)
    val point2 = Point(3,4)

    val point3 = point1 + point2
    println(point3)

    onButtonClick(onClick = { println("Click") },true)

    Database.connect()

}




