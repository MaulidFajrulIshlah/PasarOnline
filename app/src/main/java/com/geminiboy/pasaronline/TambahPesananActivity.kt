package com.geminiboy.pasaronline

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.geminiboy.pasaronline.databinding.ActivityTambahPesananBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class TambahPesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahPesananBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Storage dan Firestore
        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        // Ambil referensi tombol unggah gambar
        val btnUnggahGambar = binding.btnUnggahGambar

        // Setel listener klik pada tombol unggah gambar
        btnUnggahGambar.setOnClickListener {
            // Membuka galeri untuk memilih gambar
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        binding.btnSimpan.setOnClickListener {
            val pembeli = binding.etPembeli.text.toString()
            val penjual = binding.etPenjual.text.toString()
            val produk = binding.etProduk.text.toString()
            val timestamp = System.currentTimeMillis()

            // Simpan data pesanan baru ke Firestore
            simpanPesananBaru(pembeli, penjual, produk, timestamp)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { imageUri ->
                selectedImageUri = imageUri

                // Mengunggah gambar ke Firebase Storage
                uploadImageToFirebaseStorage()
            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // Membuat referensi file pada Firebase Storage dengan nama yang unik
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        // Mengunggah gambar ke Firebase Storage
        val uploadTask = imageRef.putFile(selectedImageUri)

        // Menambahkan listener untuk menangani hasil pengunggahan gambar
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Gambar berhasil diunggah

            // Mendapatkan URL gambar yang diunggah
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                // Simpan URL gambar ke dokumen produk di Firestore
                simpanPesananBaruDenganGambar(imageUrl)
            }
        }.addOnFailureListener { exception ->
            // Error saat mengunggah gambar
            exception.printStackTrace()
        }
    }

    private fun simpanPesananBaru(pembeli: String, penjual: String, produk: String, timestamp: Long) {
        val pesananData = hashMapOf(
            "pembeli" to pembeli,
            "penjual" to penjual,
            "produk" to produk,
            "timestamp" to timestamp
        )

        firestore.collection("pesanan")
            .add(pesananData)
            .addOnSuccessListener { documentReference ->
                // Pesanan baru berhasil disimpan ke Firestore
                finish() // Tutup aktivitas TambahPesananActivity setelah penyimpanan berhasil
            }
            .addOnFailureListener { exception ->
                // Error saat menyimpan pesanan baru ke Firestore
                exception.printStackTrace()
            }
    }

    private fun simpanPesananBaruDenganGambar(imageUrl: String) {
        val pembeli = binding.etPembeli.text.toString()
        val penjual = binding.etPenjual.text.toString()
        val produk = binding.etProduk.text.toString()
        val timestamp = System.currentTimeMillis()

        val pesananData = hashMapOf(
            "pembeli" to pembeli,
            "penjual" to penjual,
            "produk" to produk,
            "timestamp" to timestamp,
            "gambarUrl" to imageUrl
        )

        firestore.collection("pesanan")
            .add(pesananData)
            .addOnSuccessListener { documentReference ->
                // Pesanan baru dengan gambar berhasil disimpan ke Firestore
                finish() // Tutup aktivitas TambahPesananActivity setelah penyimpanan berhasil
            }
            .addOnFailureListener { exception ->
                // Error saat menyimpan pesanan baru ke Firestore
                exception.printStackTrace()
            }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}
