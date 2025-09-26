package com.wd.kotlin_basic.task3.sealedclass

import java.lang.Exception
import kotlin.random.Random

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading"
        }
    }
}

sealed class UIState {
    data object Loading : UIState()
    data class Success(val data: String) : UIState()
    data class Error(val exception: Exception) : UIState()
}

fun updateUI(state: UIState) {
    when (state) {
        is UIState.Loading -> showLoadingIndicator()
        is UIState.Success -> showData(state.data)
        is UIState.Error -> showError(state.exception)
    }
}

fun showLoadingIndicator() {
    println("Loading ...........")
}

fun showData(data: String) {
    println("Data: $data")
}

fun showError(exception: Exception) {
    println("Error: $exception")
}

fun fetchData(): Result<String> {
    val list = listOf(Result.Loading, Result.Success("Success"), Result.Error(Exception("Error")))
    return list[Random.nextInt(0, 3)]
}

fun main() {
    val states =
        listOf(UIState.Loading, UIState.Success("Success"), UIState.Error(Exception("Error")))
    states.forEach {
        updateUI(it)
    }

    val result = fetchData()
    when (result) {
        is Result.Loading -> showLoadingIndicator()
        is Result.Success -> showData(result.data)
        is Result.Error -> showError(result.exception)
    }

}