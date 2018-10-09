package com.youknow.hppk2018.angelswing.ui.list.favorites

import com.youknow.hppk2018.angelswing.data.model.Product

interface FavoritesContract {
    interface View {
        fun showProgressBar(visibility: Int)
        fun showEmptyView(visibility: Int)
        fun onProductsLoaded(products: List<Product>)

    }

    interface Presenter {
        fun unsubscribe()
        fun getFavorites()

    }
}