package com.example.perfectconsultlogger.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("call")
    fun createCallLog(@Body request: CallRequest): Call<Void>
}