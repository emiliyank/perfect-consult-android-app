package com.example.perfectconsultlogger.data.remote

import android.content.Context
import android.util.Log
import com.example.perfectconsultlogger.PushNotificationReceiver
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.remote.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiWrapper(val context: Context) {

    val TAG = "ApiWrapper"

    companion object {
        //        const val BASE_URL = "http://inveit280.voyager.icnhost.net/perfect-crm/public/api/"
        const val BASE_URL = "https://test.perfectconsult.bg/api/"

        //        const val BASE_URL = "https://perfectconsult.bg/api/"
//        const val BASE_URL = "https://baza.perfectconsult.bg/api/"
        private const val CREATE_CALL = "create_call"
        private const val CREATE_CALL_ON_FAILURE = "create_call_on_failure"
        private const val CREATE_CALL_ON_RESPONSE_SUCCESS = "create_call_on_response_success"
        private const val CREATE_CALL_ON_RESPONSE_FAILED = "create_call_on_response_failed"
        private var instance: ApiWrapper? = null

        fun getInstance(context: Context): ApiWrapper {
            if (instance == null) {
                instance = ApiWrapper(context)
            }
            return instance as ApiWrapper
        }
    }

    private var service: ApiService
    private var database: Database
    private val pushNotificationReceiver = PushNotificationReceiver()

    init {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        service = retrofit.create(ApiService::class.java)
        database = Database.getInstance(context)
    }

    fun createCallLogAsync(request: CallRequest) {
        pushNotificationReceiver.firebaseAnalyticsLogEven(CREATE_CALL)
        service.createCallLog(request).enqueue(object : retrofit2.Callback<Void?> {
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.d(TAG, t.toString())
                pushNotificationReceiver.firebaseAnalyticsLogEven(CREATE_CALL_ON_FAILURE)
            }

            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: send call ${request.phoneNumber}")
                    pushNotificationReceiver.firebaseAnalyticsLogEven(
                        CREATE_CALL_ON_RESPONSE_SUCCESS
                    )
                } else {
                    pushNotificationReceiver.firebaseAnalyticsLogEven(CREATE_CALL_ON_RESPONSE_FAILED)
                    Log.d(TAG, "Failed to send call, request response: ${response.code()}")
                }
            }
        })
    }

    fun login(email: String, password: String, callback: Callback<String>) {
        service.login(LoginRequest(email, password))
            .enqueue(object : retrofit2.Callback<LoginResponse?> {
                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    callback.onError(t.message ?: "")
                }

                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null && response.body()?.apiToken != null) {
                        callback.onDataReceived(response.body()?.apiToken ?: "")
                    } else {
                        callback.onError(response.errorBody()?.string() ?: "")
                    }
                }
            })
    }

    fun logoutWithToken(callback: Callback<Boolean>) {
        database.getUserToken(object : Database.DataListener<String> {
            override fun onData(data: String) {
                logout(data, callback)
            }
        })
    }

    private fun logout(token: String, callback: Callback<Boolean>) {
        service.logout(LogoutRequest(token)).enqueue(object : retrofit2.Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onError(t.message ?: "")
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback.onDataReceived(true)
                } else {
                    callback.onError(response.errorBody()?.string() ?: "")
                }
            }
        })
    }

    fun createCallLog(request: CallRequest): Boolean {
        val execute = service.createCallLog(request).execute()
        return execute.isSuccessful
    }

    fun sendNotificationToken(notificationToken: String) {
        database.getUserToken(object : Database.DataListener<String> {
            override fun onData(data: String) {
                service.sendNotificationToken(NotificationTokenRequest(data, notificationToken))
                    .enqueue(object : retrofit2.Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            Log.e(TAG, "request is successful: " + response.isSuccessful)
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e(TAG, "request failed", t)
                        }
                    })
            }
        })
    }

    public interface Callback<T> {
        fun onDataReceived(data: T)
        fun onError(error: String)
    }
}
