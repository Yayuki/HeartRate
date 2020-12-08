package yayuki.heartrate

import yayuki.heartrate.error.ErrorCode

sealed class MyResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : MyResult<T>()
    data class Error(val code: ErrorCode) : MyResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$code]"
        }
    }
}