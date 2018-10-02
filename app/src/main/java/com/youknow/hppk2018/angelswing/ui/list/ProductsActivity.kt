package com.youknow.hppk2018.angelswing.ui.list

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.youknow.hppk2018.angelswing.R

class ProductsActivity : AppCompatActivity(), ProductsContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
