package com.youknow.hppk2018.angelswing.ui.list.search

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.ui.KEY_PRODUCT_ID
import com.youknow.hppk2018.angelswing.ui.details.DetailsActivity
import com.youknow.hppk2018.angelswing.ui.list.ProductClickListener
import com.youknow.hppk2018.angelswing.ui.list.ProductsAdapter
import com.youknow.hppk2018.angelswing.ui.list.REQUEST_CODE_ADD_EDIT_PRODUCT
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.toast

class SearchActivity : AppCompatActivity(), SearchContract.View, View.OnClickListener, ProductClickListener {

    private lateinit var mPresenter: SearchContract.Presenter
    private lateinit var mAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mPresenter = SearchPresenter(this)
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(etSearch.text.toString())
                true
            }

            false
        }
        ivSearch.setOnClickListener(this)

        mAdapter = ProductsAdapter(this, this)

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rvProducts.adapter = mAdapter
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivSearch -> search(etSearch.text.toString())
        }
    }

    override fun showProgressBar(visible: Int) {
        progressBar.visibility = visible
    }

    override fun showEmptyView(visible: Int) {
        tvEmptyProducts.visibility = visible
    }

    override fun onProductsLoaded(products: List<Product>) {
        mAdapter.products = products.toMutableList()
        mAdapter.notifyDataSetChanged()
    }

    override fun onClickProduct(product: Product) {
        startActivityForResult(Intent(this, DetailsActivity::class.java).putExtra(KEY_PRODUCT_ID, product.id), REQUEST_CODE_ADD_EDIT_PRODUCT)
    }

    private fun search(keyword: String) {
        if (TextUtils.isEmpty(keyword)) {
            toast(R.string.err_search_keyword_is_empty)
            return
        }

        mPresenter.search(keyword)
    }
}