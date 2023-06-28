package com.geminiboy.pasaronline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.geminiboy.pasaronline.databinding.ActivityUbahPesananBinding
import com.google.firebase.firestore.FirebaseFirestore

class UbahPesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUbahPesananBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var pesananId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        pesananId = intent.getStringExtra("pesananId")!!

        binding.btnSimpan.setOnClickListener {
            val pembeli = binding.etPembeli.text.toString()
            val penjual = binding.etPenjual.text.toString()
            val produk = binding.etProduk.text.toString()

            val timestamp = System.currentTimeMillis()

            if (pembeli.isNotEmpty() && penjual.isNotEmpty() && produk.isNotEmpty()) {
                ubahPesanan(pesananId, pembeli, penjual, produk, timestamp)
            } else {
                Toast.makeText(this, "Mohon lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun ubahPesanan(pesananId: String, newPembeli: String, newPenjual: String, newProduk: String, newTimestamp: Long) {
        val pesananDocRef = firestore.collection("pesanan").document(pesananId)

        val updatedPesananData = HashMap<String, Any?>()
        updatedPesananData["pembeli"] = newPembeli
        updatedPesananData["penjual"] = newPenjual
        updatedPesananData["produk"] = newProduk
        updatedPesananData["timestamp"] = newTimestamp

        pesananDocRef.set(updatedPesananData)
            .addOnSuccessListener {
                // Pesanan berhasil diubah di Firestore
            }
            .addOnFailureListener { exception ->
                // Error saat mengubah pesanan di Firestore
                exception.printStackTrace()
            }
    }

}
