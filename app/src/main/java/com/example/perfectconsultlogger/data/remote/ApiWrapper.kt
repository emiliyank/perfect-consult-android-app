package com.example.perfectconsultlogger.data.remote

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiWrapper() {

    val TAG = "ApiWrapper"

    companion object{
        const val BASE_URL = "http://inveit280.voyager.icnhost.net/perfect-crm/public/api/"
    }

    var service: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        service = retrofit.create(ApiService::class.java)
    }

    fun createCallLogAsync(request: CallRequest){
        service.createCallLog(request).enqueue(object : retrofit2.Callback<Void?> {
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if(response.isSuccessful) {
                    Log.d(TAG, "Success: send call ${request.otherPhone}")
                }else{
                    Log.d(TAG, "Failed to send call, request response: ${response.code()}")
                }
            }
        })
    }

    fun createCallLog(request: CallRequest): Boolean {
        val execute = service.createCallLog(request).execute()
        return execute.isSuccessful
    }
}