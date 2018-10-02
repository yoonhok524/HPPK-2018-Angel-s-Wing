package com.youknow.hppk2018.angelswing.ui.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug


class LoginActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val actionCodeSettings = ActionCodeSettings.newBuilder()
                // URL you want to redirect back to. The domain (www.example.com) for this
                // URL must be whitelisted in the Firebase Console.
                .setUrl("https://hppk-2018-angel-s-wing.firebaseapp.com")
                .setHandleCodeInApp(true)
                .setAndroidPackageName(
                        "com.youknow.hppk2018.angelswing",
                        true, /* installIfNotAvailable */
                        "1"    /* minimumVersion */)
                .build()

        tvSendSignInLink.setOnClickListener {
            val email = "${etEmail.text}@hp.com"
            debug("[HPPK] email: $email")

            FirebaseAuth.getInstance()
                    .sendSignInLinkToEmail(email, actionCodeSettings)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            debug("[HPPK] email sent to $email")
                        } else {
                            error("[HPPK] send email failed -  $email")
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
        }
    }
}