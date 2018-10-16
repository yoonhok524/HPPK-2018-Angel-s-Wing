package com.youknow.hppk2018.angelswing.ui.details

import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.exceptions.ProductDeleteCanceledException
import com.youknow.hppk2018.angelswing.data.exceptions.ProductDeleteFailureException
import com.youknow.hppk2018.angelswing.data.exceptions.ProductSaveCanceledException
import com.youknow.hppk2018.angelswing.data.exceptions.ProductSaveFailureException
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ProductDataSource
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
import com.youknow.hppk2018.angelswing.ui.list.RESULT_CODE_DELETE_PRODUCT
import com.youknow.hppk2018.angelswing.ui.list.RESULT_CODE_EDIT_PRODUCT
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
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
        info("[HPPK] getProduct - $productId")
        disposable.add(productDataSource.getProduct(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onProductLoaded(it)
                }, {
                    removeFavoriteProduct(productId)
                }))
    }

    private fun removeFavoriteProduct(productId: String) {
        info("[HPPK] removeFavoriteProduct - $productId")
        val userEmail = FirebaseAuth.getInstance().currentUser!!.email!!
        userDataSource.removeFavoriteProduct(userEmail, productId)
                .subscribe({
                    view.showMessage(R.string.err_product_not_exist)
                    view.terminate()
                }, {
                    view.showMessage(R.string.err_product_not_exist)
                    view.terminate()
                })
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
                    info("[HPPK] deleteProduct - success: $product")
                    view.showMessage(R.string.msg_delete_success)
                    view.terminate(product, RESULT_CODE_DELETE_PRODUCT)
//                    view.terminate()
                }, {
                    it.printStackTrace()
                    when (it) {
                        is ProductDeleteFailureException -> view.showMessage(R.string.err_delete_failed)
                        is ProductDeleteCanceledException -> ""
                    }
                }))
    }

    override fun soldOut(product: Product) {
        product.onSale = false
        disposable.add(productDataSource.saveProduct(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    info("[HPPK] soldOut - success: $product")
                    view.terminate(product, RESULT_CODE_EDIT_PRODUCT)
//                    view.terminate()
                }, {
                    it.printStackTrace()
                    when (it) {
                        is ProductSaveFailureException -> view.showMessage(R.string.failed)
                        is ProductSaveCanceledException -> ""
                    }
                })
        )
    }

    override fun registerFavorite(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser!!.email!!

        val favProd = productDataSource.addFavoriteUser(product.id, userId)
        val favUser = userDataSource.addFavoriteProduct(userId, product)

        disposable.add(Single.zip(favProd, favUser, BiFunction<String, Boolean, Boolean> { productId, userSuccess ->
            !(TextUtils.isEmpty(productId) || !userSuccess)
        }).subscribe({
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

        disposable.add(Single.zip(favProd, favUser, BiFunction<String, Boolean, Boolean> { p, u ->
            !(TextUtils.isEmpty(p) || !u)
        }).subscribe({
            if (it) {
                getFavorites(product.id)
            } else {

            }
        }, {

        }))
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}
