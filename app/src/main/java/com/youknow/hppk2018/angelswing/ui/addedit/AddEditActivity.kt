package com.youknow.hppk2018.angelswing.ui.addedit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.youknow.hppk2018.angelswing.R
import kotlinx.android.synthetic.main.activity_addedit_product.*

class AddEditActivity: AppCompatActivity(), AddEditContract.View, View.OnClickListener {
    private lateinit var mPresenter: AddEditContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addedit_product)

        mPresenter = AddEditPresenter(this)

        btnRegister.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.btnRegister -> mPresenter.saveProduct(etProductName.text.toString(), etPrice.text.toString())
        }
    }

}