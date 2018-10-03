package com.youknow.hppk2018.angelswing.ui.signin

import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import com.google.gson.Gson
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_USER
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class SignInPresenter(
        private val view: SignInContract.View,
        private val pref: SharedPreferences,
        private val userDataSource: UserDataSource = UserDataSource(),
        private val disposable: CompositeDisposable = CompositeDisposable()
) : SignInContract.Presenter, AnkoLogger {

    override fun register(name: String, email: String, lab: String, part: String) {
        info("[HPPK] register: $name, $email, $lab/$part")

        if (TextUtils.isEmpty(name)) {
            view.showInvalidName(View.VISIBLE)
            return
        }
        view.showInvalidName(View.GONE)

        if (TextUtils.isEmpty(email)) {
            view.showInvalidEmail(View.VISIBLE)
            return
        }
        view.showInvalidEmail(View.GONE)

        view.showProgressBar(View.VISIBLE)
        val user = User(email, name, lab, part)
        disposable.add(userDataSource.saveUser(user)
                .subscribe({
                    view.showProgressBar(View.GONE)
                    view.registerDone(it)
                    if (it) {
                        info("[HPPK] register - success")
                        pref.edit().putString(KEY_USER, Gson().toJson(user)).apply()
                    } else {
                        error("[HPPK] register - failed")
                    }
                }, {
                    view.showProgressBar(View.GONE)
                    view.registerDone(false)
                    it.printStackTrace()
                }))
    }

    override fun unsubscribe() {
        info("[HPPK] unsubscribe")
        disposable.clear()
    }

}