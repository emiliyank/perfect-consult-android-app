package com.example.perfectconsultlogger.data.remote

import retrofit2.Call
import retrofit2.Response

class ApiWrapper(private val service: ApiService) {
    companion object{
        const val BASE_URL = "http://inveit280.voyager.icnhost.net/perfect-crm/public/api"
    }

    fun createCallLog(token: String){
        service.createCallLog(token).enqueue(object : retrofit2.Callback<Void?> {
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}