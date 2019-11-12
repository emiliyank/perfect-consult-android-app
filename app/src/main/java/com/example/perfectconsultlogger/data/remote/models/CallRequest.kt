package com.example.perfectconsultlogger.data.remote.models

import com.google.gson.annotations.SerializedName

class CallRequest(@SerializedName("api_token") val apiToken: String,
                  val phoneNumber: String, val startTime: String, val duration: Long, val callType: String) {}
