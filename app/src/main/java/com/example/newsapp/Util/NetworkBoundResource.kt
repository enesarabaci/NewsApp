package com.example.newsapp.Util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.lang.Exception

@ExperimentalCoroutinesApi
inline fun <T, K> networkBoundResource(
    crossinline query: () -> Flow<T>,
    crossinline fetch: suspend () -> K,
    crossinline saveFetchResult: suspend (K) -> Unit,
    crossinline shouldFetch: (T) -> Boolean = {true}
) = channelFlow<Resource<*>> {
    val data = query().first()
    send(Resource.Loading)

    if (shouldFetch(data)) {
        try {
            saveFetchResult(fetch())
            try {
                query().collect { send(Resource.Success(it)) }
            }catch (e: Exception) {
                send(Resource.Error(e.localizedMessage ?: "Error!", null))
            }
        }catch (e: Exception) {
            try {
                query().collect { send(Resource.Error(e.localizedMessage ?: "Error", it)) }
            }catch (e: Exception) {
                send(Resource.Error(e.localizedMessage ?: "Error!", null))
            }
        }
    }else {
        try {
            query().collect { send(Resource.Success(it)) }
        }catch (e: Exception) {
            send(Resource.Error(e.localizedMessage ?: "Error!", null))
        }
    }
}