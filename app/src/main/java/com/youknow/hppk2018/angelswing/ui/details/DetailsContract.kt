package com.youknow.hppk2018.angelswing.ui.details

import com.youknow.hppk2018.angelswing.data.model.Product

interface DetailsContract {
    interface View {
        fun showProgressBar(visible: Int)
        fun terminate()
        fun showError(errMsg: Int)

    }

    interface Presenter {
        fun deleteProduct(product: Product)
        fun unsubscribe()

    }
}