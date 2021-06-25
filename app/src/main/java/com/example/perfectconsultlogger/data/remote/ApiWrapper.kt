package com.example.perfectconsultlogger.data.remote

import android.content.Context
import android.util.Log
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.remote.models.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class ApiWrapper(val context: Context) {

    val TAG = "ApiWrapper"

    companion object {
        const val BASE_URL = "https://test.perfectconsult.bg/api/"

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

    init {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient())
            .build()
        service = retrofit.create(ApiService::class.java)
        database = Database.getInstance(context)
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager


            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun createCallLogAsync(request: CallRequest) {
        service.createCallLog(request).enqueue(object : retrofit2.Callback<Void?> {
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Success: send call ${request.phoneNumber}")
                } else {
                    Log.d(TAG, "Failed to send call, request response: ${response.code()}")
                }
            }
        })
    }

    fun login(email: String, password: String, callback: Callback<String>) {
        service.login(LoginRequest(email, password)).enqueue(object : retrofit2.Callback<LoginResponse?> {
            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                callback.onError(t.message ?: "")
            }

            override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {
                if (response.isSuccessful && response.body() != null && response.body()?.apiToken != null) {
                    callback.onDataReceived(response.body()?.apiToken ?: "")
                } else {
                    callback.onError(response.errorBody()?.string() ?: "")
                }
            }
        })
    }

    fun logoutWithToken(callback: Callback<Boolean>) {
        database.getUserToken(object: Database.DataListener<String> {
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
        database.getUserToken(object: Database.DataListener<String> {
            override fun onData(data: String) {
                service.sendNotificationToken(NotificationTokenRequest(data, notificationToken)).enqueue(object : retrofit2.Callback<Void> {
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
