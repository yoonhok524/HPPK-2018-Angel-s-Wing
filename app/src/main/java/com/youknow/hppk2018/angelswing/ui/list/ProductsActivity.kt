package com.youknow.hppk2018.angelswing.ui.list

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.ui.login.LoginActivity
import org.jetbrains.anko.toast

class ProductsActivity : AppCompatActivity(), ProductsContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            toast("Hello ${FirebaseAuth.getInstance().currentUser!!.displayName}")
        }
    }
}
