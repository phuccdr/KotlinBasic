package com.wd.kotlin_basic.task3.coroutine

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wd.kotlin_basic.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MainScope: Su dung Dispatcher.Main cho cac coroutine va co SupervisorJob.
 * public fun MainScope(): CoroutineScope = ContextScope(SupervisorJob() + Dispatchers.Main)
 * Vi su dung SupervisorJob() nen khi coroutine bi loi khong anh huong den cac coroutine khac.
 */
class MainActivity : AppCompatActivity() {
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainScope.launch {
            drawUI()
        }
        mainScope.launch {
            drawIcon()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}

suspend fun drawUI() {
    delay(500)
    Log.d(
        "MainActivity",
        "Running on ${Thread.currentThread().name} - ${Thread.currentThread().hashCode()}"
    )
    Log.d("MainActivity", "Draw UI")
}

suspend fun drawIcon() {
    delay(2000)
    Log.d(
        "MainActivity",
        "Running on ${Thread.currentThread().name} - ${Thread.currentThread().hashCode()}"
    )
    Log.d("MainActivity", "Draw Icon")
}