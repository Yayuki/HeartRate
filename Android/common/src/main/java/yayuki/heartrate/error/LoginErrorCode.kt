package yayuki.heartrate.error


enum class LoginErrorCode : ErrorCode {
    CREDENTIALS_ERROR, TIME_OUT, EXTRA_TYPE_ERROR;

    companion object {
        fun getException(index: Int): ErrorCode {
            val values = values()
            return if (index in 0..values.lastIndex) values[index]
            else BaseErrorCode.UNKNOWN
        }
    }
}

