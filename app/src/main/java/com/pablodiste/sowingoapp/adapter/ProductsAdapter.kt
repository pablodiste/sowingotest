package com.pablodiste.sowingoapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.checkbox.MaterialCheckBox
import com.pablodiste.sowingoapp.R
import com.pablodiste.sowingoapp.data.model.Product
import com.pablodiste.sowingoapp.databinding.ItemProductBinding

class ProductsAdapter(private val listener: OnItemClickListener): ListAdapter<Product, ProductsAdapter.ProductViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val product = getItem(position)
                        //TODO: Implement click
                    }
                }
                registerFavoriteListener(cbFavorite)
            }
        }

        private fun registerFavoriteListener(cbFavorite: MaterialCheckBox) {
            cbFavorite.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = getItem(position)
                    listener.onFavoriteCheckedChange(product, isChecked)
                }
            }
        }

        private fun removeFavoriteListener(cbFavorite: MaterialCheckBox) {
            cbFavorite.setOnCheckedChangeListener(null)
        }

        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView)
                    .load(product.main_image)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(ivImage)
                tvTitle.text = product.name
                tvPrice.text = "$" + product.vendor_inventory?.firstOrNull()?.price?.toString() ?: ""
                tvListPrice.text = "$" + product.vendor_inventory?.firstOrNull()?.list_price?.toString() ?: ""
                tvListPrice.paintFlags = tvListPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                with (cbFavorite) {
                    removeFavoriteListener(this)
                    cbFavorite.isChecked = product.isFavorite
                    registerFavoriteListener(this)
                }
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(product: Product)
        fun onFavoriteCheckedChange(product: Product, isFavorite: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.objectID == newItem.objectID
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}