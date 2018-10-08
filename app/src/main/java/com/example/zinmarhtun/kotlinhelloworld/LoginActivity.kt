package com.example.zinmarhtun.kotlinhelloworld

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_btn_login.setOnClickListener {
            val email = email_edittext__register.text.toString()
            val password = password_edittext_register.text.toString()

            Log.d("LoginActivity >> ", "Attempt Login with email/password >> $email/****")
        }

        back_to_register_textview.setOnClickListener {
            finish()
        }

    }

}