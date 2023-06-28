package com.geminiboy.pasaronline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.geminiboy.pasaronline.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.actHome -> {
                    // Tidak perlu melakukan apa-apa karena Anda sudah berada di halaman Home
                    true
                }
                R.id.actAkun -> {
                    val intent = Intent(this, Akun::class.java)
                    startActivity(intent)
                    true
                }
                R.id.actPesanan -> {
                    val intent = Intent(this, PesananActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


        binding.keAkun.setOnClickListener {
            val intent = Intent(this, Akun::class.java)
            startActivity(intent)
        }


        // Inisialisasi RecyclerView
        binding.productRecyclerView.layoutManager = LinearLayoutManager(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username")
                    username?.let {
                        val welcomeMessage = "Selamat Datang, $username"
                        binding.tvSelamatDatang.text = welcomeMessage
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }


    }
}
