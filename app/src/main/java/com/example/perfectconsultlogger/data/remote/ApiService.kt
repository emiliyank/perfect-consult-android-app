package com.example.perfectconsultlogger.data.remote

import com.example.perfectconsultlogger.data.remote.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("call")
    fun createCallLog(@Body request: CallRequest): Call<Void>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("logout")
    fun logout(@Body request: LogoutRequest): Call<Void>

    @POST("notification")
    fun sendNotificationToken(@Body request: NotificationTokenRequest): Call<Void>
}
