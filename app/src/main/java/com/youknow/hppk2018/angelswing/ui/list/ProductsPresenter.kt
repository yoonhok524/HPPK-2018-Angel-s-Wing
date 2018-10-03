package com.youknow.hppk2018.angelswing.ui.list

import android.view.View
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.error

class ProductsPresenter(
        private val view: ProductsContract.View,
        private val productDataSource: ProductDataSource = ProductDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
): ProductsContract.Presenter, AnkoLogger {

    override fun getProducts() {
        disposable.add(productDataSource.getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it == null || it.isEmpty()) {
                        view.showEmptyView(View.VISIBLE)
                    } else {
                        view.showEmptyView(View.GONE)
                        view.onProductsLoaded(it)
                    }
                }, {
                    error("[HPPK] getProducts - failed: ${it.message}")
                    it.printStackTrace()
                }))
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}