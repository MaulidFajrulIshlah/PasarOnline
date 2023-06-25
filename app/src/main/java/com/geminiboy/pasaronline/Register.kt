package com.geminiboy.pasaronline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.geminiboy.pasaronline.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        binding.btnregis.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmPassword = binding.confirmPassEt.text.toString()
            val username = binding.usernameEt.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && username.isNotEmpty()) {
                if (isValidEmail(email)) {
                    if (password == confirmPassword) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val currentUser = firebaseAuth.currentUser
                                currentUser?.let { user ->
                                    val userMap = hashMapOf(
                                        "username" to username,
                                        "email" to email,
                                        "password" to password
                                    )
                                    firestore.collection("users").document(user.uid)
                                        .set(userMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(this@Register, "Registration Successful", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@Register, Home::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this@Register, e.message, Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Toast.makeText(this@Register, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@Register, "Password does not match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Register, "Invalid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@Register, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
}
