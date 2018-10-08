package com.youknow.hppk2018.angelswing.ui.list

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.ui.KEY_USER
import com.youknow.hppk2018.angelswing.ui.addedit.AddEditActivity
import com.youknow.hppk2018.angelswing.ui.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_products.*
import org.jetbrains.anko.*

class ProductsActivity : AppCompatActivity(), ProductsContract.View, View.OnClickListener, AnkoLogger {
    private lateinit var mPresenter: ProductsContract.Presenter

    private lateinit var mAdapter: ProductsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        setTitle(R.string.product_list)

        mPresenter = ProductsPresenter(this)
        mAdapter = ProductsAdapter(this)

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rvProducts.adapter = mAdapter

        fabAddProduct.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        mPresenter.getProducts()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.fabAddProduct -> onClickAddProduct()
        }
    }

    override fun showProgressBar(visible: Int) {
        progressBar.visibility = visible
    }

    override fun showEmptyView(visibility: Int) {
        tvEmptyProducts.visibility = visibility
    }

    override fun onProductsLoaded(products: List<Product>) {
        mAdapter.products = products
        mAdapter.notifyDataSetChanged()
    }

    private fun onClickAddProduct() {
        if (isNeedSignIn()) {
            alert(R.string.privacy_policy_details, R.string.privacy_policy) {
                yesButton {
                    startActivity(Intent(this@ProductsActivity, SignInActivity::class.java))
                    finish()
                }
                noButton {}
            }.show()
        } else {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }

    private fun isNeedSignIn() = FirebaseAuth.getInstance().currentUser == null || !PreferenceManager.getDefaultSharedPreferences(this).contains(KEY_USER)
}
