package com.geminiboy.pasaronline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.geminiboy.pasaronline.databinding.ActivityPesananBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class PesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPesananBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: PesananAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTambahPesanan.setOnClickListener {
            val intent = Intent(this, TambahPesananActivity::class.java)
            startActivity(intent)
        }

        firestore = FirebaseFirestore.getInstance()

        // Tampilkan daftar pesanan
        tampilkanDaftarPesanan()

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
                    // Tidak perlu melakukan apa pun karena Anda sudah berada di PesananActivity
                    true
                }
                else -> false
            }
        }
    }

    private fun tampilkanDaftarPesanan() {
        val pesananCollection = firestore.collection("pesanan")

        pesananCollection.get()
            .addOnSuccessListener { documents ->
                val daftarPesanan = mutableListOf<Pesanan>()
                for (document in documents) {
                    val id = document.id
                    val pembeli = document.getString("pembeli")!!
                    val penjual = document.getString("penjual")!!
                    val produk = document.getString("produk")!!
                    val timestamp = document.getLong("timestamp")!!

                    // Buat objek Pesanan dari data yang diperoleh
                    val pesanan = Pesanan(id, pembeli, penjual, produk, timestamp)

                    // Tambahkan objek Pesanan ke dalam daftarPesanan
                    daftarPesanan.add(pesanan)
                }

                // Buat adapter PesananAdapter dengan daftarPesanan
                adapter = PesananAdapter(daftarPesanan)

                // Set adapter ke RecyclerView
                binding.recyclerViewPesanan.adapter = adapter

                // Tambahkan listener klik pada adapter
                adapter.setOnItemClickListener { pesanan ->
                    getPesananDetail(pesanan.id)
                }

                // Tambahkan listener klik ubah pada adapter
                adapter.setOnUpdateItemClickListener { pesanan ->
                    ubahPesanan(pesanan)
                }
            }
            .addOnFailureListener { exception ->
                // Error saat mengambil daftar pesanan dari Firestore
                exception.printStackTrace()
            }
    }

    private fun getPesananDetail(pesananId: String) {
        val pesananDocRef = firestore.collection("pesanan").document(pesananId)

        pesananDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val pembeli = document.getString("pembeli")
                    val penjual = document.getString("penjual")
                    val produk = document.getString("produk")
                    val timestamp = document.getLong("timestamp")

                    // Tampilkan data pesanan ke tampilan detail pesanan
                    // Implementasikan logika tampilan yang sesuai di sini
                    val intent = Intent(this, DetailPesananActivity::class.java)
                    intent.putExtra("pembeli", pembeli)
                    intent.putExtra("penjual", penjual)
                    intent.putExtra("produk", produk)
                    intent.putExtra("timestamp", timestamp)
                    startActivity(intent)
                } else {
                    // Dokumen pesanan tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Error saat mengambil data pesanan dari Firestore
                exception.printStackTrace()
            }
    }

    private fun hapusPesanan(pesananId: String) {
        val pesananDocRef = firestore.collection("pesanan").document(pesananId)

        pesananDocRef.delete()
            .addOnSuccessListener {
                // Pesanan berhasil dihapus dari Firestore
            }
            .addOnFailureListener { exception ->
                // Error saat menghapus pesanan dari Firestore
                exception.printStackTrace()
            }
    }

    private fun ubahPesanan(pesanan: Pesanan) {
        val intent = Intent(this, UbahPesananActivity::class.java)
        intent.putExtra("pesananId", pesanan.id)
        intent.putExtra("pembeli", pesanan.pembeli)
        intent.putExtra("penjual", pesanan.penjual)
        intent.putExtra("produk", pesanan.produk)
        intent.putExtra("timestamp", pesanan.timestamp)
        startActivity(intent)
    }

    // ...
}
