package com.example.perfectconsultlogger.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.perfectconsultlogger.PushNotificationReceiver.Companion.NOTIFICATION_PHONE_NUMBER_PAYLOAD
import com.example.perfectconsultlogger.R
import com.example.perfectconsultlogger.data.Database
import com.example.perfectconsultlogger.data.remote.ApiWrapper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val database: Database
        get() {
            return Database.getInstance(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database.getUserToken(object: Database.DataListener<String> {
            override fun onData(data: String) {
                if(data.isNotEmpty()) {
                    showMainScreen()
                } else {
                    setContentView(R.layout.activity_login)
                    btnLogin.setOnClickListener { onLoginClicked() }
                }
            }
        })
    }

    private fun onLoginClicked() {
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()
        if(isEmailValid(email) && isPasswordValid(password)) {
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val service = ApiWrapper.getInstance(this)
        service.login(email, password, object: ApiWrapper.Callback<String> {
            override fun onDataReceived(data: String) {
                database.setUserToken(data)
                sendNotificationToken()
                showMainScreen()
            }

            override fun onError(error: String) {
                showError(error)
            }
        })
    }

    private fun sendNotificationToken() {
        database.getNotificationToken(object: Database.DataListener<String> {
            override fun onData(data: String) {
                if(data.isNotEmpty()) ApiWrapper.getInstance(applicationContext).sendNotificationToken(data)
            }
        })
    }

    private fun showError(error: String) {
        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_LONG).show()
    }

    private fun isPasswordValid(password: String): Boolean {
        return when {
            password.isBlank() -> {
                edtPassword.error = getString(R.string.error_field_mandatory)
                false
            }
            password.length < 6 -> {
                edtPassword.error = getString(R.string.error_password_short)
                false
            }
            else -> true
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return when {
            email.isBlank() -> {
                edtEmail.error = getString(R.string.error_field_mandatory)
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                edtEmail.error = getString(R.string.error_bad_format_email)
                false
            }
            else -> true
        }
    }

    private fun showMainScreen() {
        val mainActivity = Intent(this, MainActivity::class.java)
        intent.extras?.getString(NOTIFICATION_PHONE_NUMBER_PAYLOAD)?.let {
            mainActivity.putExtra(NOTIFICATION_PHONE_NUMBER_PAYLOAD, it)
        }

        startActivity(mainActivity)
        finish()
    }
}
