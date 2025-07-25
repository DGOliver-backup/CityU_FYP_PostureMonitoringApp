package com.example.o1mlkit4

object  VideoFilePathPassing {
    var storedString: String? = null

    fun setString(newString: String) {
        storedString = newString
    }

    fun getString(): String? {
        return storedString
    }
}