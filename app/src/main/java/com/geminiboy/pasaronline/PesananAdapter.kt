package com.geminiboy.pasaronline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geminiboy.pasaronline.databinding.ItemPesananBinding

class PesananAdapter(private val daftarPesanan: List<Pesanan>) :
    RecyclerView.Adapter<PesananAdapter.PesananViewHolder>() {

    private var onItemClickListener: ((Pesanan) -> Unit)? = null
    private var onUpdateItemClickListener: ((Pesanan) -> Unit)? = null

    fun setOnItemClickListener(listener: (Pesanan) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnUpdateItemClickListener(listener: (Pesanan) -> Unit) {
        onUpdateItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PesananViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_pesanan,
            parent,
            false
        )
        return PesananViewHolder(view)
    }

    override fun onBindViewHolder(holder: PesananViewHolder, position: Int) {
        val pesanan = daftarPesanan[position]
        holder.bind(pesanan)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(pesanan)
        }
        holder.btnUbah.setOnClickListener {
            onUpdateItemClickListener?.invoke(pesanan)
        }
    }

    override fun getItemCount(): Int {
        return daftarPesanan.size
    }

    inner class PesananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemPesananBinding = ItemPesananBinding.bind(itemView)

        val btnUbah: Button = binding.btnHapus

        fun bind(pesanan: Pesanan) {
            binding.tvPembeli.text = pesanan.pembeli
            binding.tvPenjual.text = pesanan.penjual
            binding.tvProduk.text = pesanan.produk
            binding.tvTimestamp.text = pesanan.timestamp.toString()
        }
    }
}
