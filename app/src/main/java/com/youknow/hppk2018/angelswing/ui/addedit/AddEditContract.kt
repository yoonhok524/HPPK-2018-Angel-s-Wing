package com.youknow.hppk2018.angelswing.ui.addedit

import com.youknow.hppk2018.angelswing.data.model.User


interface AddEditContract {
    interface View {
        fun showProgressBar(visibility: Int)
        fun terminate()
        fun failedRegisterProduct()
        fun showInvalidName(visible: Int)
        fun showInvalidPrice(visible: Int)

    }

    interface Presenter {
        fun saveProduct(name: String, price: String)
        fun unsubscribe()

    }
}