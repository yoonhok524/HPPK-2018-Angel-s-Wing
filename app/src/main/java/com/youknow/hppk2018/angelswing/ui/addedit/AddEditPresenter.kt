package com.youknow.hppk2018.angelswing.ui.addedit

import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.data.source.ImageDataSource
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_USER
import com.youknow.hppk2018.angelswing.ui.MINIMUM_TARGET_PRICE
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class AddEditPresenter(
        private val view: AddEditContract.View,
        private val userDataSource: UserDataSource = UserDataSource(),
        private val productDataSource: ProductDataSource = ProductDataSource(),
        private val imageDataSource: ImageDataSource = ImageDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : AddEditContract.Presenter, AnkoLogger {

    private lateinit var seller: User

    init {
        val id = FirebaseAuth.getInstance().currentUser!!.email!!
        disposable.add(userDataSource.getUser(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    seller = it
                }, {

                }))
    }

    override fun saveProduct(name: String, txtPrice: String) {

        if (TextUtils.isEmpty(name)) {
            view.showInvalidName(View.VISIBLE)
            return
        }
        view.showInvalidName(View.GONE)

        if (TextUtils.isEmpty(txtPrice)) {
            view.showInvalidPrice(View.VISIBLE, R.string.invalid_price)
            return
        }

        val price = txtPrice.toInt()
        if (price < MINIMUM_TARGET_PRICE) {
            view.showInvalidPrice(View.VISIBLE, R.string.invalid_money_more_than_5000)
            return
        }
        view.showInvalidPrice(View.GONE, R.string.invalid_price)

        view.showProgressBar(View.VISIBLE)

        val product = Product(name = name, price = price, seller = seller)
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

    override fun saveProduct(name: String, txtPrice: String, imgBytes: ByteArray) {

        if (TextUtils.isEmpty(name)) {
            view.showInvalidName(View.VISIBLE)
            return
        }
        view.showInvalidName(View.GONE)

        if (TextUtils.isEmpty(txtPrice)) {
            view.showInvalidPrice(View.VISIBLE, R.string.invalid_price)
            return
        }

        val price = txtPrice.toInt()
        if (price < MINIMUM_TARGET_PRICE) {
            view.showInvalidPrice(View.VISIBLE, R.string.invalid_money_more_than_5000)
            return
        }
        view.showInvalidPrice(View.GONE, R.string.invalid_price)

        view.showProgressBar(View.VISIBLE)

        var imgFileName = "${seller.name}_${System.currentTimeMillis()}.jpg"
        val imageUploader = imageDataSource.saveImage(imgBytes, imgFileName)

        val product = Product(name = name, price = price, seller = seller, imgFileName = imgFileName)
        info("[HPPK] saveProduct - $product")

        val productRegister = productDataSource.saveProduct(product)

        disposable.add(productRegister.flatMap {
            if (it) {
                imageUploader
            } else {
                Single.create { it.onSuccess(false) }
            }
        }.subscribe({
            info("[HPPK] saveProduct - done: $it")
            view.showProgressBar(View.GONE)
            if (it) {
                view.terminate()
            } else {
                view.failedRegisterProduct()
            }
        }, {
            it.printStackTrace()
        }))
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}