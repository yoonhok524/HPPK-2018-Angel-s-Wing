package com.youknow.hppk2018.angelswing.ui.details

import com.youknow.hppk2018.angelswing.data.model.Product

interface DetailsContract {
    interface View {
        fun onProductLoaded(product: Product)
        fun showProgressBar(visible: Int)
        fun terminate()
        fun showMessage(errMsg: Int)
        fun showFavorites(isFavorite: Boolean)
        fun onFavoriteNumberLoaded(favoriteNumber: Int)

    }

    interface Presenter {
        fun getProduct(productId: String)
        fun getFavorites(productId: String)
        fun isFavorite(productId: String)
        fun deleteProduct(product: Product)
        fun soldOut(product: Product)
        fun unsubscribe()
        fun registerFavorite(product: Product)
        fun unregisterFavorite(product: Product)

    }
}