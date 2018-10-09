package com.youknow.hppk2018.angelswing.ui.list.favorites

import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoritesPresenter(
        private val view: FavoritesContract.View,
        private val userDataSource: UserDataSource = UserDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : FavoritesContract.Presenter {

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun getFavorites() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.email)) {
            disposable.add(userDataSource.getFavorites(firebaseUser.email!!)
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
                        error("[HPPK] getFavorites - failed: ${it.message}")
                        it.printStackTrace()
                    }))
        }
    }

}