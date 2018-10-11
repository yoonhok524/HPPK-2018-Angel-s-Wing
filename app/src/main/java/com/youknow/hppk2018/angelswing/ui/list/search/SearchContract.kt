package com.youknow.hppk2018.angelswing.ui.list.search

import com.youknow.hppk2018.angelswing.data.model.Product

interface SearchContract {
    interface View {
        fun showProgressBar(visible: Int)
        fun showEmptyView(visible: Int)
        fun onProductsLoaded(products: List<Product>)

    }

    interface Presenter {
        fun unsubscribe()
        fun search(keyword: String)

    }
}