package com.example.perfectconsultlogger.data.remote

import com.example.perfectconsultlogger.data.remote.models.CallRequest
import com.example.perfectconsultlogger.data.remote.models.LoginRequest
import com.example.perfectconsultlogger.data.remote.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("call")
    fun createCallLog(@Body request: CallRequest): Call<Void>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}