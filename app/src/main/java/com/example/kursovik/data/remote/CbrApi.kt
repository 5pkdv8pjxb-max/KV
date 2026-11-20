package com.example.kursovik.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface CbrApi {

    @GET("scripts/XML_daily.asp")
    suspend fun getDailyRates(@Query("date_req") date: String? = null): String

    @GET("scripts/XML_dynamic.asp")
    suspend fun getDynamicRates(
        @Query("date_req1") date1: String,
        @Query("date_req2") date2: String,
        @Query("VAL_NM_RQ") valuteCode: String
    ): String
}