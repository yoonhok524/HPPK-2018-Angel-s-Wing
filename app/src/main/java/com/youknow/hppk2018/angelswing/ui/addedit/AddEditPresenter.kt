package com.youknow.hppk2018.angelswing.ui.addedit

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.data.source.ImageDataSource
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
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
        view.showProgressBar(View.VISIBLE)
        val price = txtPrice.toInt()
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
        view.showProgressBar(View.VISIBLE)
        val price = txtPrice.toInt()

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

    override fun editProduct(product: Product, name: String, txtPrice: String) {
        view.showProgressBar(View.VISIBLE)

        product.name = name
        product.price = txtPrice.toInt()
        info("[HPPK] editProduct - $product")

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

    override fun editProduct(product: Product, name: String, txtPrice: String, imgBytes: ByteArray) {
        view.showProgressBar(View.VISIBLE)

        product.name = name
        product.price = txtPrice.toInt()
        product.imgFileName = "${seller.name}_${System.currentTimeMillis()}.jpg"
        info("[HPPK] editProduct - $product")

        val imageUploader = imageDataSource.saveImage(imgBytes, product.imgFileName)
        val productRegister = productDataSource.saveProduct(product)

        disposable.add(productRegister.flatMap {
            if (it) {
                imageUploader
            } else {
                Single.create { it.onSuccess(false) }
            }
        }.subscribe({
            info("[HPPK] editProduct - done: $it")
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