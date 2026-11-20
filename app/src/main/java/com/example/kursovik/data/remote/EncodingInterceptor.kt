package com.example.kursovik.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.charset.Charset

class EncodingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val body = response.body ?: return response

        val rawBytes = body.bytes()
        val decoded = String(rawBytes, Charset.forName("windows-1251"))

        val newBody = decoded.toResponseBody("text/xml; charset=utf-8".toMediaTypeOrNull())

        return response.newBuilder()
            .body(newBody)
            .build()
    }
}