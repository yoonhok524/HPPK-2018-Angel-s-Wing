package com.youknow.hppk2018.angelswing.ui.list

import com.youknow.hppk2018.angelswing.data.model.Product

interface ProductsContract {
    interface View {
        fun showProgressBar(visible: Int)
        fun showEmptyView(visibility: Int)
        fun onProductsLoaded(products: List<Product>)

    }

    interface Presenter {
        fun unsubscribe()
        fun getProducts()
        fun getMyFavoriteProducts()

    }
}