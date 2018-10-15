package com.youknow.hppk2018.angelswing.ui.details

import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DetailsPresenter(
        val view: DetailsContract.View,
        private val userDataSource: UserDataSource = UserDataSource(),
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
                    val userEmail = FirebaseAuth.getInstance().currentUser!!.email!!
                    userDataSource.removeFavoriteProduct(userEmail, productId)
                            .subscribe({
                                view.showMessage(R.string.err_product_not_exist)
                                view.terminate()
                            }, {
                                view.showMessage(R.string.err_product_not_exist)
                                view.terminate()
                            })
                }))
    }

    override fun getFavorites(productId: String) {
        disposable.add(productDataSource.getFavoriteNumber(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val num = it ?: 0
                    view.onFavoriteNumberLoaded(num)
                }, {
                    view.onFavoriteNumberLoaded(0)
                }))
    }

    override fun isFavorite(productId: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.email)) {
            disposable.add(userDataSource.getFavorites(firebaseUser.email!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val product = it.find { it.id == productId }
                        view.showFavorites(product != null)
                    }, {
                        view.showFavorites(false)
                    }))
        }
    }

    override fun deleteProduct(product: Product) {
        view.showProgressBar(View.VISIBLE)
        disposable.add(productDataSource.delete(product.id)
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

    override fun registerFavorite(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser!!.email!!

        val favProd = productDataSource.addFavoriteUser(product.id, userId)
        val favUser = userDataSource.addFavoriteProduct(userId, product)

        disposable.add(favProd.mergeWith(favUser).subscribeOn(Schedulers.io())
                .subscribe({
                    if (it) {
                        getFavorites(product.id)
                    }
                }, {

                }))
    }

    override fun unregisterFavorite(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser!!.email!!

        val favProd = productDataSource.removeFavoriteUser(product.id, userId)
        val favUser = userDataSource.removeFavoriteProduct(userId, product.id)

        disposable.add(favProd.mergeWith(favUser).subscribeOn(Schedulers.io())
                .subscribe({
                    if (it) {
                        getFavorites(product.id)
                    }
                }, {

                }))
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}