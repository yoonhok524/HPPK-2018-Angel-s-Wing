package com.youknow.hppk2018.angelswing.ui.list.favorites

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.ui.list.ProductsAdapter
import kotlinx.android.synthetic.main.activity_products.*

class FavoritesActivity : AppCompatActivity(), FavoritesContract.View {
    private lateinit var mPresenter: FavoritesContract.Presenter
    private lateinit var mAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        mPresenter = FavoritesPresenter(this)
        mAdapter = ProductsAdapter(this)

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rvProducts.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()
        mPresenter.getFavorites()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun showProgressBar(visible: Int) {
        progressBar.visibility = visible
    }

    override fun showEmptyView(visibility: Int) {
        tvEmptyProducts.visibility = visibility
    }

    override fun onProductsLoaded(products: List<Product>) {
        mAdapter.products = products.toMutableList()
        mAdapter.notifyDataSetChanged()
    }
}