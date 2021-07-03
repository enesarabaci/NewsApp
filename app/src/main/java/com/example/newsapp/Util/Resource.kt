package com.example.newsapp.Util

sealed class Resource<T>(val data: T? = null, val errorMessage: String? = null) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(errorMessage: String, data: T?) : Resource<T>(data = data ,errorMessage = errorMessage)
    object Loading : Resource<Any>()
}
