package com.youknow.hppk2018.angelswing.ui.signin

import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_USER
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class SignInPresenter(
        private val view: SignInContract.View,
        private val pref: SharedPreferences,
        private val userDataSource: UserDataSource = UserDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : SignInContract.Presenter, AnkoLogger {

    override fun register(name: String, hpAccount: String, lab: String, part: String) {
        info("[HPPK] register: $name, $hpAccount, $lab/$part")

        if (TextUtils.isEmpty(name)) {
            view.showInvalidName(View.VISIBLE)
            return
        }
        view.showInvalidName(View.GONE)

        if (TextUtils.isEmpty(hpAccount)) {
            view.showInvalidEmail(View.VISIBLE)
            return
        }
        view.showInvalidEmail(View.GONE)

        view.showProgressBar(View.VISIBLE)
        val user = User(FirebaseAuth.getInstance().currentUser!!.email!!, name, hpAccount, lab, part)
        disposable.add(userDataSource.saveUser(user)
                .subscribe({
                    view.showProgressBar(View.GONE)
                    view.registerDone(it)
                    info("[HPPK] register - complete: $it")
                    if (it) {
                        pref.edit().putString(KEY_USER, Gson().toJson(user)).apply()
                    }
                }, {
                    view.showProgressBar(View.GONE)
                    view.registerDone(false)
                    it.printStackTrace()
                }))
    }

    override fun isAlreadyExistUser(id: String) {
        info("[HPPK] isAlreadyExistUser: $id")
        disposable.add(userDataSource.getUser(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.id == id) {
                        pref.edit().putString(KEY_USER, Gson().toJson(it)).apply()
                        view.registerDone(true)
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