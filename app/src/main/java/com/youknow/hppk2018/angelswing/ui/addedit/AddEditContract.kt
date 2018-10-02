package com.youknow.hppk2018.angelswing.ui.addedit

interface AddEditContract {
    interface View {

    }

    interface Presenter {
        abstract fun saveProduct(name: String, price: String)

    }
}