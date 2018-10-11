package com.youknow.hppk2018.angelswing.ui.list

import com.google.firebase.firestore.DocumentSnapshot

interface ProductsContract {
    interface View {
        fun showProgressBar(visible: Int)
        fun showEmptyView(visibility: Int)
        fun onProductsLoaded(documentSnapshots: List<DocumentSnapshot>)

    }

    interface Presenter {
        fun unsubscribe()
        fun getProducts()
        fun getNextProducts(last: DocumentSnapshot)
    }
}