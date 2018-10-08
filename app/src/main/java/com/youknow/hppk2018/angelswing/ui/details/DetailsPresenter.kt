package com.youknow.hppk2018.angelswing.ui.details

import android.view.View
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DetailsPresenter(
        val view: DetailsContract.View,
        private val productDataSource: ProductDataSource = ProductDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : DetailsContract.Presenter, AnkoLogger {

    override fun getProduct(productId: String) {
        disposable.add(productDataSource.getProduct(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onProductLoaded(it)
                }, {
                    view.showMessage(R.string.err_product_not_exist)
                    view.terminate()
                }))
    }

    override fun deleteProduct(product: Product) {
        view.showProgressBar(View.VISIBLE)
        disposable.add(productDataSource.delete(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        view.showMessage(R.string.msg_delete_success)
                        view.terminate()
                    } else {
                        view.showMessage(R.string.err_delete_failed)
                    }
                }, {

                }))
    }

    override fun soldOut(product: Product) {
        product.onSale = false
        disposable.add(productDataSource.saveProduct(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        view.terminate()
                    } else {
                        view.showMessage(R.string.failed)
                    }
                }, {

                })
        )
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}