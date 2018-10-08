package com.youknow.hppk2018.angelswing.ui.addedit

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.data.model.Product
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

    private val mId = FirebaseAuth.getInstance().currentUser!!.email!!

    override fun saveProduct(name: String, txtPrice: String) {
        view.showProgressBar(View.VISIBLE)
        val price = txtPrice.toInt()

        disposable.add(userDataSource.getUser(mId)
                .flatMap { seller ->
                    val product = Product(name = name, price = price, seller = seller)
                    info("[HPPK] saveProduct - $product")
                    productDataSource.saveProduct(product)
                }
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

        disposable.add(userDataSource.getUser(mId)
                .flatMap { seller ->
                    var imgFileName = "${seller.name}_${System.currentTimeMillis()}.jpg"
                    val imageUploader = imageDataSource.saveImage(imgBytes, imgFileName)

                    val product = Product(name = name, price = price, seller = seller, imgFileName = imgFileName)
                    info("[HPPK] saveProduct - $product")

                    val productRegister = productDataSource.saveProduct(product)
                    productRegister.flatMap {
                        if (it) {
                            imageUploader
                        } else {
                            Single.create { it.onSuccess(false) }
                        }
                    }
                }
                .subscribe({
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
        disposable.add(userDataSource.getUser(mId)
                .flatMap { seller ->
                    product.name = name
                    product.price = txtPrice.toInt()
                    product.imgFileName = "${seller.name}_${System.currentTimeMillis()}.jpg"
                    info("[HPPK] editProduct - $product")

                    val imageUploader = imageDataSource.saveImage(imgBytes, product.imgFileName)
                    productDataSource.saveProduct(product).flatMap {
                        if (it) {
                            imageUploader
                        } else {
                            Single.create { it.onSuccess(false) }
                        }
                    }
                }
                .subscribe({
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