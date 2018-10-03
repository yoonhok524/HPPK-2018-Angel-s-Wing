package com.youknow.hppk2018.angelswing.ui.addedit

import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import com.google.gson.Gson
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_USER
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class AddEditPresenter(
        private val view: AddEditContract.View,
        pref: SharedPreferences,
        private val productDataSource: ProductDataSource = ProductDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : AddEditContract.Presenter, AnkoLogger {

    private val seller: User

    init {
        val jsonUser = pref.getString(KEY_USER, "")
        if (TextUtils.isEmpty(jsonUser)) {
            throw IllegalStateException("User need to sign in")
        }

        seller = Gson().fromJson(jsonUser, User::class.java)
    }

    override fun saveProduct(name: String, txtPrice: String) {

        if (TextUtils.isEmpty(name)) {
            view.showInvalidName(View.VISIBLE)
            return
        }
        view.showInvalidName(View.GONE)

        if (TextUtils.isEmpty(txtPrice)) {
            view.showInvalidPrice(View.VISIBLE)
            return
        }
        view.showInvalidPrice(View.GONE)

        val price = txtPrice.toInt()

        view.showProgressBar(View.VISIBLE)
        val product = Product(name, price = price, seller = seller)
        info("[HPPK] saveProduct - $product")

        disposable.add(productDataSource.saveProduct(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.showProgressBar(View.GONE)
                    if (it) {
                        view.terminate()
                    } else {
                        view.failedRegisterProduct()
                    }
                }, {

                }))
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}