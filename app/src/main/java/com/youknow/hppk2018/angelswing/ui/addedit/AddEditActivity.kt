package com.youknow.hppk2018.angelswing.ui.addedit

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.youknow.hppk2018.angelswing.R
import kotlinx.android.synthetic.main.activity_addedit_product.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast

class AddEditActivity : AppCompatActivity(), AddEditContract.View, View.OnClickListener {

    private lateinit var mPresenter: AddEditContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addedit_product)

        mPresenter = AddEditPresenter(this, PreferenceManager.getDefaultSharedPreferences(this))

        btnRegister.setOnClickListener(this)
        ivProduct.setOnClickListener(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnRegister -> mPresenter.saveProduct(etProductName.text.toString(), etPrice.text.toString())
            R.id.ivProduct -> openGetImageDialog()
        }
    }

    override fun showProgressBar(visibility: Int) {
        progressBar.visibility = visibility
    }

    override fun terminate() {
        finish()
    }

    override fun failedRegisterProduct() {
        alert(R.string.failed_register_product).show()
    }

    override fun showInvalidName(visible: Int) {
        tvErrProductName.visibility = visible
    }

    override fun showInvalidPrice(visible: Int) {
        tvErrPrice.visibility = visible
    }

    private fun openGetImageDialog() {
        val countries = listOf(getString(R.string.camera), getString(R.string.gallery))
        selector(getString(R.string.choose_get_image_method), countries) { _, i ->
            toast("${countries[i]} is selected")
        }
    }

}