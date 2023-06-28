package com.geminiboy.pasaronline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.geminiboy.pasaronline.databinding.ActivityDetailPesananBinding

class DetailPesananActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Implementasikan logika tampilan dan pengolahan data untuk detail pesanan di sini
        // Anda dapat menggunakan getIntent() untuk mengambil data yang dikirim dari PesananActivity
        val pembeli = intent.getStringExtra("pembeli")
        val penjual = intent.getStringExtra("penjual")
        val produk = intent.getStringExtra("produk")
        val timestamp = intent.getLongExtra("timestamp", 0)

        // Contoh: Tampilkan data pesanan ke dalam TextView
        binding.tvPembeli.text = pembeli
        binding.tvPenjual.text = penjual
        binding.tvProduk.text = produk
        binding.tvTimestamp.text = timestamp.toString()
    }
}
