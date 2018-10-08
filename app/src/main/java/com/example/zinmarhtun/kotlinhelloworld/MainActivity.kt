package com.example.zinmarhtun.kotlinhelloworld

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity >> ", "Hello 123 ...")

        register_btn_register.setOnClickListener {

            val email = email_edittext__register.text.toString()
            val password = password_edittext_register.text.toString()

            Log.d("MainActivity -> ", "Email : " + email)
            Log.d("MainActivity -> ", "Password : " + password)

            // firebase authentication to create user with email and password

        }

        already_have_acc_textview.setOnClickListener {
            Log.d("MainActivity -> ", "try to show login activity...")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
