package com.youknow.hppk2018.angelswing.ui.addedit

import com.youknow.hppk2018.angelswing.data.model.Product


interface AddEditContract {
    interface View {
        fun showProgressBar(visibility: Int)
        fun terminate()
        fun failedRegisterProduct()

    }

    interface Presenter {
        fun saveProduct(name: String, price: String)
        fun saveProduct(name: String, price: String, bytes: ByteArray)
        fun editProduct(product: Product, name: String, price: String)
        fun editProduct(product: Product, name: String, price: String, bytes: ByteArray)
        fun unsubscribe()

    }
}