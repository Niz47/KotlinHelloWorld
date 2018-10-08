package com.example.zinmarhtun.kotlinhelloworld

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity >> ", "Hello 123 ...")

        register_btn_register.setOnClickListener {
            performRegister()
        }

        already_have_acc_textview.setOnClickListener {
            Log.d("MainActivity -> ", "try to show login activity...")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        val email = email_edittext__register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in Email/ Password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity -> ", "Email : " + email)
        Log.d("MainActivity -> ", "Password : " + password)

        // firebase authentication to create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (!it.isSuccessful) return@addOnCompleteListener
                    // if successful
                    Log.d("MainActivity >> ","Successfully created user with uid : ${it.result?.user?.uid}")

                }
                .addOnFailureListener {
                    Log.d("MainActivity >> ", "Failed to create user : ${it.message}")
                }
    }
}
