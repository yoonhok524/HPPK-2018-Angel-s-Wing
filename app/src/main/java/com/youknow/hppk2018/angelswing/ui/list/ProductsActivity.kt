package com.youknow.hppk2018.angelswing.ui.list

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.ui.KEY_USER
import com.youknow.hppk2018.angelswing.ui.addedit.AddEditActivity
import com.youknow.hppk2018.angelswing.ui.list.favorites.FavoritesActivity
import com.youknow.hppk2018.angelswing.ui.signin.SignInActivity
import com.youknow.hppk2018.angelswing.ui.statistics.StatisticsActivity
import kotlinx.android.synthetic.main.activity_products.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class ProductsActivity : AppCompatActivity(), ProductsContract.View, View.OnClickListener, AnkoLogger {

    private lateinit var mPresenter: ProductsContract.Presenter
    private lateinit var mAdapter: ProductsAdapter
    private lateinit var mMenuFavorites: MenuItem
    private lateinit var mProducts: List<DocumentSnapshot>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        setTitle(R.string.product_list)

        mPresenter = ProductsPresenter(this)
        mAdapter = ProductsAdapter(this)

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rvProducts.adapter = mAdapter
        rvProducts.setOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && mProducts.size == 20) {
                        mPresenter.getNextProducts(mProducts.last())
                    }

                }
            }
        })

        fabAddProduct.setOnClickListener(this)
        mPresenter.getProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        mMenuFavorites = menu.findItem(R.id.favorites)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.statistics -> startActivity(Intent(this, StatisticsActivity::class.java))
            R.id.favorites -> startActivity(Intent(this, FavoritesActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fabAddProduct -> onClickAddProduct()
        }
    }

    override fun showProgressBar(visible: Int) {
        progressBar.visibility = visible
    }

    override fun showEmptyView(visibility: Int) {
        tvEmptyProducts.visibility = visibility
    }

    override fun onProductsLoaded(documentSnapshots: List<DocumentSnapshot>) {
        mProducts = documentSnapshots
        mAdapter.products.addAll(mProducts.map {
            it.toObject(Product::class.java)!!
        }.toList())

        mAdapter.notifyDataSetChanged()
        initOptionsMenu()
    }

    private fun initOptionsMenu() {
        val me = FirebaseAuth.getInstance().currentUser
        if (me != null) {
            mMenuFavorites.isVisible = true
        }
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
