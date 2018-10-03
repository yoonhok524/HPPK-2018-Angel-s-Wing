package com.youknow.hppk2018.angelswing.ui.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import kotlinx.android.synthetic.main.item_product.view.*
import java.text.NumberFormat
import java.util.*

class ProductsAdapter(
        private val context: Context,
        private val products: List<Product> = listOf()
): RecyclerView.Adapter<ProductsAdapter.ProductHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false))

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]

        holder.itemView.tvProdName.text = product.name

        val format = NumberFormat.getCurrencyInstance(Locale.KOREA)
        val price = format.format(product.price)

        holder.itemView.tvPrice.text = price
        holder.itemView.tvSellerName.text = product.seller.name
        holder.itemView.tvSellerLabPart.text = "${product.seller.lab} | ${product.seller.part}"

        if (!TextUtils.isEmpty(product.photoUrl)) {
            Glide.with(context).load(product.photoUrl).into(holder.itemView.ivProduct)
        }
    }

    class ProductHolder(view: View): RecyclerView.ViewHolder(view)
}