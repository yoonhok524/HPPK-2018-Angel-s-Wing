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
        view.showProgressBar(View.VISIBLE)
        disposable.add(productDataSource.getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showProgressBar(View.GONE)
                    if (it == null || it.isEmpty()) {
                        view.showEmptyView(View.VISIBLE)
                        view.onProductsLoaded(listOf())
                    } else {
                        view.showEmptyView(View.GONE)
                        view.onProductsLoaded(it)
                    }
                }, {
                    view.showProgressBar(View.GONE)
                    error("[HPPK] getChartData - failed: ${it.message}")
                    it.printStackTrace()
                }))
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}