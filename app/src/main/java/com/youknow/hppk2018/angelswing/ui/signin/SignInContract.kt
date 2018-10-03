package com.youknow.hppk2018.angelswing.ui.signin

interface SignInContract {
    interface View {
        fun showInvalidName(visibility: Int)
        fun showInvalidEmail(visibility: Int)
        fun registerDone(success: Boolean)
        fun showProgressBar(visible: Int)

    }

    interface Presenter {
        fun register(name: String, email: String, lab: String, part: String)
        fun isAlreadyExistUser(email: String)
        fun unsubscribe()

    }
}