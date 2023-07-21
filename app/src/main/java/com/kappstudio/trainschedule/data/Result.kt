package com.kappstudio.trainschedule.data

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Fail(val error: String) : Result<Nothing>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[result=$data]"
            is Fail -> "Fail[error=$error]"
            is Error -> "Error[exception=${exception.message}]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [Result] is of catalogType [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null