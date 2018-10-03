package com.youknow.hppk2018.angelswing.ui.addedit


interface AddEditContract {
    interface View {
        fun showProgressBar(visibility: Int)
        fun terminate()
        fun failedRegisterProduct()
        fun showInvalidName(visible: Int)
        fun showInvalidPrice(visible: Int, msg: Int)

    }

    interface Presenter {
        fun saveProduct(name: String, price: String)
        fun saveProduct(name: String, price: String, bytes: ByteArray)
        fun unsubscribe()

    }
}