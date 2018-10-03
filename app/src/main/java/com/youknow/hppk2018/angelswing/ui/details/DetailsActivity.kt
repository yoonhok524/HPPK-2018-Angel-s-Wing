package com.youknow.hppk2018.angelswing.ui.details

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.GlideApp
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ImageDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_PRODUCT
import com.youknow.hppk2018.angelswing.ui.addedit.AddEditActivity
import com.youknow.hppk2018.angelswing.utils.getFormattedPrice
import kotlinx.android.synthetic.main.activity_details.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class DetailsActivity : AppCompatActivity(), DetailsContract.View {

    private lateinit var mProduct: Product

    private lateinit var mPresenter: DetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mPresenter = DetailsPresenter(this)
        mProduct = intent.getParcelableExtra(KEY_PRODUCT)

        tvProductName.text = mProduct.name
        collapsingToolbar.title = mProduct.name
        collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.invisible))

        tvPrice.text = getFormattedPrice(mProduct.price)
        tvSellerName.text = mProduct.seller.name
        tvSellerLabPart.text = "${mProduct.seller.lab} | ${mProduct.seller.part}"

        if (!TextUtils.isEmpty(mProduct.imgFileName)) {
            val imgRef = ImageDataSource().getImageRef(mProduct.imgFileName)
            GlideApp.with(this)
                    .load(imgRef)
                    .into(ivProduct)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val me = FirebaseAuth.getInstance().currentUser
        if (me != null && me.email == mProduct.seller.id) {
            menuInflater.inflate(R.menu.details, menu)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.delete -> showDeletePopup()
            R.id.edit -> startActivity(Intent(this, AddEditActivity::class.java).putExtra(KEY_PRODUCT, mProduct))
        }

        return true
    }

    private fun showDeletePopup() {
        alert(R.string.msg_product_delete, R.string.product_delete) {
            yesButton {
                mPresenter.deleteProduct(mProduct)
            }
            noButton {}
        }.show()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun showProgressBar(visible: Int) {
        progressBar.visibility = visible
    }

    override fun terminate() {
        toast(R.string.msg_delete_success)
        finish()
    }

    override fun showError(errMsg: Int) {
        toast(errMsg)
    }
}
