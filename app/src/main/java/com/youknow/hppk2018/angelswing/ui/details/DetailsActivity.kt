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
import com.youknow.hppk2018.angelswing.ui.KEY_PRODUCT_ID
import com.youknow.hppk2018.angelswing.ui.addedit.AddEditActivity
import com.youknow.hppk2018.angelswing.utils.getFormattedPrice
import kotlinx.android.synthetic.main.activity_details.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class DetailsActivity : AppCompatActivity(), DetailsContract.View {

    private lateinit var mPresenter: DetailsPresenter
    private lateinit var mProduct: Product

    private lateinit var mMenuEdit: MenuItem
    private lateinit var mMenuDelete: MenuItem
    private lateinit var mMenuSoldout: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mPresenter = DetailsPresenter(this)
    }

    override fun onStart() {
        super.onStart()
        val productId = intent.getStringExtra(KEY_PRODUCT_ID)
        mPresenter.getProduct(productId)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details, menu)

        mMenuEdit = menu.findItem(R.id.edit)
        mMenuDelete = menu.findItem(R.id.delete)
        mMenuSoldout = menu.findItem(R.id.soldOut)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.delete -> showDeletePopup()
            R.id.edit -> startActivity(Intent(this, AddEditActivity::class.java).putExtra(KEY_PRODUCT, mProduct))
            R.id.soldOut -> showSoldOutPopup()
        }

        return true
    }

    override fun onProductLoaded(product: Product) {
        mProduct = product
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

        initOptionsMenu()
    }

    private fun initOptionsMenu() {
        val me = FirebaseAuth.getInstance().currentUser
        if (me != null && me.email == mProduct.seller.id) {
            mMenuEdit.isVisible = true
            mMenuDelete.isVisible = true
            mMenuSoldout.isVisible = true
        }
    }

    private fun showDeletePopup() {
        alert(R.string.msg_product_delete, R.string.product_delete) {
            yesButton {
                mPresenter.deleteProduct(mProduct)
            }
            noButton {}
        }.show()
    }

    private fun showSoldOutPopup() {
        alert(R.string.msg_sold_out, R.string.sold_out) {
            yesButton {
                mPresenter.soldOut(mProduct)
            }
            noButton {}
        }.show()
    }

}
