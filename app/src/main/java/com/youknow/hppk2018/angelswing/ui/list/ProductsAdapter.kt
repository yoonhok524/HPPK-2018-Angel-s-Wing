package com.youknow.hppk2018.angelswing.ui.list

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.youknow.hppk2018.angelswing.GlideApp
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ImageDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_PRODUCT
import com.youknow.hppk2018.angelswing.ui.details.DetailsActivity
import com.youknow.hppk2018.angelswing.utils.getFormattedPrice
import kotlinx.android.synthetic.main.item_product.view.*
import java.text.NumberFormat
import java.util.*

class ProductsAdapter(
        private val context: Context,
        private val products: List<Product> = listOf(),
        private val imageDataSource: ImageDataSource = ImageDataSource()
) : RecyclerView.Adapter<ProductsAdapter.ProductHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false))

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]

        holder.itemView.tvProdName.text = product.name

        val price = getFormattedPrice(product.price)

        holder.itemView.tvLblPrice.text = price
        holder.itemView.tvSellerName.text = product.seller.name
        holder.itemView.tvSellerLabPart.text = "${product.seller.lab} | ${product.seller.part}"

        if (!TextUtils.isEmpty(product.imgFileName)) {
            val imgRef = imageDataSource.getImageRef(product.imgFileName)
            GlideApp.with(context)
                    .load(imgRef)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(holder.itemView.ivProduct)
        }

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, DetailsActivity::class.java)
                    .putExtra(KEY_PRODUCT, product))
        }
    }

    class ProductHolder(view: View) : RecyclerView.ViewHolder(view)
}