package com.example.mymvvmnewsapp.util

/* Sealed class is kind of an abstract class but we can decide
 which classes are allowed to inherit from a sealed class. */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}