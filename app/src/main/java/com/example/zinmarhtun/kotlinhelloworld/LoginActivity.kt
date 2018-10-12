package com.example.zinmarhtun.kotlinhelloworld

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_btn_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            Log.d("LoginActivity >> ", "Attempt Login with email/password >> $email/****")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in: success
                        val intent = Intent(this, LatestMessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        // Sign in: fail
                        Log.e("LoginActivity", "signIn: Fail!", task.exception)
                        Toast.makeText(this, "signIn: Fail! Please Try Again ...", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        back_to_register_textview.setOnClickListener {
            finish()
        }

    }

}