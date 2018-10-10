package com.example.zinmarhtun.kotlinhelloworld

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Log.d("RegisterActivity >> ", "Hello 123 ...")

        register_btn_register.setOnClickListener {
            performRegister()
        }

        already_have_acc_textview.setOnClickListener {
            Log.d("RegisterActivity -> ", "try to show login activity...")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_btn_register.setOnClickListener {
            Log.d("RegisterActivity >> ", "try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null)
        {
            Log.d("RegisterActivity >> ", "Photo was selected...")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            select_photo_btn_register.alpha = 0f

            /*val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_btn_register.setBackgroundDrawable(bitmapDrawable)*/
        }
        else {
            Log.d("RegisterActivity >> ", "Photo was not selected...")
        }
    }

    private fun performRegister(){
        val email = email_edittext__register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in Email/ Password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity -> ", "Email : " + email)
        Log.d("RegisterActivity -> ", "Password : " + password)

        // firebase authentication to create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (!it.isSuccessful) return@addOnCompleteListener
                    // if successful
                    Log.d("RegisterActivity >> ","Successfully created user with uid : ${it.result?.user?.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity >> ", "Failed to create user : ${it.message}")
                    Toast.makeText(this, "Failed to create user : ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToFirebaseStorage() {
        Log.d("RegisterActivity >>", "Before upload photo")
        Log.d("RegisterActivity >> ", "selectedPhotoUri : $selectedPhotoUri ...")
        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Please select profile picture", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener{
                    Log.d("RegisterActivity >>", "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnCompleteListener {
                        Log.d("RegisterActivity >>", "File Location : ${it.toString()}")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity >>", "File Upload Failure")
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_edittext_register.text.toString(), profileImageUrl)
        ref.setValue(user)
                .addOnCompleteListener {
                    Log.d("RegisterActivity >>", "Finally we saved new user to firebase database")
                }
    }
}

class User(val uid:String, val username:String, val profileImageUrl:String)
