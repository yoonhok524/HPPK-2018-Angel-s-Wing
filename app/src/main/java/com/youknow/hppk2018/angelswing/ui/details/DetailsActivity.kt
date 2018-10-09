package com.youknow.hppk2018.angelswing.ui.details

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.GlideApp
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ImageDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_PRODUCT
import com.youknow.hppk2018.angelswing.ui.KEY_PRODUCT_ID
import com.youknow.hppk2018.angelswing.ui.KEY_USER
import com.youknow.hppk2018.angelswing.ui.addedit.AddEditActivity
import com.youknow.hppk2018.angelswing.ui.signin.SignInActivity
import com.youknow.hppk2018.angelswing.utils.getFormattedPrice
import kotlinx.android.synthetic.main.activity_details.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class DetailsActivity : AppCompatActivity(), DetailsContract.View, View.OnClickListener {

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
        chkFavorite.setOnClickListener(this)
        onFavoriteNumberLoaded(0)
    }

    override fun onStart() {
        super.onStart()
        val productId = intent.getStringExtra(KEY_PRODUCT_ID)
        mPresenter.getProduct(productId)
        mPresenter.getFavorites(productId)
        mPresenter.isFavorite(productId)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun showProgressBar(visible: Int) {
        progressBar.visibility = visible
    }

    override fun terminate() {
        finish()
    }

    override fun showMessage(errMsg: Int) {
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

    override fun onFavoriteNumberLoaded(favoriteNumber: Int) {
        if (favoriteNumber == 0) {
            tvFavoriteNumber.visibility = View.GONE
        } else {
            tvFavoriteNumber.visibility = View.VISIBLE
            tvFavoriteNumber.text = getString(R.string.number_of_employees_interested_in_this_product, favoriteNumber)
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.chkFavorite -> favoriteChanged()
        }
    }

    private fun initOptionsMenu() {
        val me = FirebaseAuth.getInstance().currentUser
        if (me != null && me.email == mProduct.seller.id) {
            mMenuEdit.isVisible = true
            mMenuDelete.isVisible = true
            mMenuSoldout.isVisible = true
        }
    }

    private fun favoriteChanged() {
        if (isNeedSignIn()) {
            alert(R.string.privacy_policy_details, R.string.privacy_policy) {
                yesButton {
                    startActivity(Intent(this@DetailsActivity, SignInActivity::class.java))
                    finish()
                }
                noButton {}
            }.show()
        } else {
            if (chkFavorite.isChecked) {
                mPresenter.registerFavorite(mProduct)
            } else {
                mPresenter.unregisterFavorite(mProduct)
            }
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

    override fun showFavorites(isFavorites: Boolean) {
        chkFavorite.isChecked = isFavorites
    }

    private fun isNeedSignIn() = FirebaseAuth.getInstance().currentUser == null || !PreferenceManager.getDefaultSharedPreferences(this).contains(KEY_USER)

}
