package com.youknow.hppk2018.angelswing.ui.list.search

import android.view.View
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchPresenter(
        private val view: SearchContract.View,
        private val productDataSource: ProductDataSource = ProductDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : SearchContract.Presenter {

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun search(keyword: String) {
        view.showProgressBar(View.VISIBLE)
        productDataSource.getAllProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    view.showProgressBar(View.GONE)
                    val searchResults = list.filter { it.name.contains(keyword) }.toList()
                    if (searchResults == null || searchResults.isEmpty()) {
                        view.showEmptyView(View.VISIBLE)
                    } else {
                        view.onProductsLoaded(searchResults)
                    }
                }, {
                    view.showEmptyView(View.VISIBLE)
                })
    }

}