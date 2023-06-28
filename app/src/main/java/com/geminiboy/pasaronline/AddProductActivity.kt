package com.geminiboy.pasaronline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.geminiboy.pasaronline.databinding.ActivityAddProductBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.actHome -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
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

        binding.btnChooseImage.setOnClickListener {
            // Menampilkan galeri untuk memilih gambar produk
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        binding.btnAddProduct.setOnClickListener {
            uploadProductImage()
        }
    }

    private fun uploadProductImage() {
        selectedImageUri?.let { uri ->
            // Membuat nama unik untuk gambar produk
            val imageName = UUID.randomUUID().toString()

            // Mendefinisikan path file di Firebase Storage
            val imageRef = storageReference.child("product_images/$imageName")

            // Mengunggah gambar produk ke Firebase Storage
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    // Mendapatkan URL gambar yang diunggah
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        // Menyimpan URL gambar produk ke Firebase Firestore
                        saveProductToFirestore(imageUrl.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    // Menampilkan pesan error jika gagal mengunggah gambar
                    exception.printStackTrace()
                }
        }
    }

    private fun saveProductToFirestore(imageUrl: String) {
        // Mendapatkan data produk dari inputan pengguna
        val productName = binding.etProductName.text.toString()
        val productDescription = binding.etProductDescription.text.toString()
        val productPrice = binding.etProductPrice.text.toString().toDouble()
        val sellerName = binding.etSellerName.text.toString()

        // Membuat objek data produk
        val productData = hashMapOf(
            "name" to productName,
            "description" to productDescription,
            "price" to productPrice,
            "sellerName" to sellerName,
            "image" to imageUrl
        )

        // Menyimpan data produk ke Firebase Firestore
        firestore.collection("products")
            .add(productData)
            .addOnSuccessListener {
                // Menampilkan pesan berhasil
                // atau melakukan tindakan lain setelah berhasil menyimpan data produk
                finish()
            }
            .addOnFailureListener { exception ->
                // Menampilkan pesan error jika gagal menyimpan data produk
                exception.printStackTrace()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            // Mendapatkan URI gambar yang dipilih dari galeri
            selectedImageUri = data?.data

            // Menampilkan gambar yang dipilih ke ImageView
            selectedImageUri?.let {
                binding.ivProductImage.setImageURI(it)
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE = 100
    }
}
