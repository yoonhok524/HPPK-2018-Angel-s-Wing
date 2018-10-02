package com.youknow.hppk2018.angelswing.ui.signin

import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.data.source.UserDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_USER_ID
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

        if (TextUtils.isEmpty(email)) {
            view.showInvalidEmail(View.VISIBLE)
            return
        }

        view.showInvalidName(View.GONE)
        view.showInvalidEmail(View.GONE)
        view.showProgressBar(View.VISIBLE)

        disposable.add(userDataSource.saveUser(User(email, name, lab, part))
                .subscribe({
                    view.showProgressBar(View.GONE)
                    view.registerDone(it)
                    if (it) {
                        info("[HPPK] register - success")
                        pref.edit().putString(KEY_USER_ID, email).apply()
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