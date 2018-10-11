package com.example.zinmarhtun.kotlinhelloworld

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.zinmarhtun.kotlinhelloworld.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Log.d(TAG, "Hello 123 ...")

        register_btn_register.setOnClickListener {
            performRegister()
        }

        already_have_acc_textview.setOnClickListener {
            Log.d(TAG, "try to show login activity...")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_btn_register.setOnClickListener {
            Log.d(TAG, "try to show photo selector")

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
            Log.d(TAG, "Photo was selected...")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            select_photo_btn_register.alpha = 0f

            /*val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_btn_register.setBackgroundDrawable(bitmapDrawable)*/
        }
        else {
            Log.d(TAG, "Photo was not selected...")
        }
    }

    private fun performRegister(){
        val email = email_edittext__register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in Email/ Password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Email : " + email)
        Log.d(TAG, "Password : " + password)

        // firebase authentication to create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (!it.isSuccessful) return@addOnCompleteListener
                    // if successful
                    Log.d(TAG,"Successfully created user with uid : ${it.result?.user?.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to create user : ${it.message}")
                    Toast.makeText(this, "Failed to create user : ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToFirebaseStorage() {
        Log.d(TAG, "Before upload photo")
        Log.d(TAG, "selectedPhotoUri : $selectedPhotoUri ...")
        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Please select profile picture", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener{
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File Location : $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "File Upload Failure")
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        Log.d(TAG, "UID : $uid")
        Log.d(TAG, "Image URL : $profileImageUrl")
        Log.d(TAG, "UserName : ${username_edittext_register.text.toString()}")
        val db = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)
        Log.d(TAG, "Firebase DB RF : $db")
        Log.d(TAG, "User obj : ${user.toString()}")
        db.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved new user to firebase database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}