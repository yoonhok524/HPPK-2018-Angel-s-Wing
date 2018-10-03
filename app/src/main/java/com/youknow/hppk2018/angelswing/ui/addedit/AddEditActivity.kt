package com.youknow.hppk2018.angelswing.ui.addedit

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_addedit_product.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import com.bumptech.glide.Glide
import com.youknow.hppk2018.angelswing.GlideApp
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.source.ImageDataSource
import com.youknow.hppk2018.angelswing.ui.KEY_PRODUCT
import com.youknow.hppk2018.angelswing.ui.MODE_EDIT
import com.youknow.hppk2018.angelswing.ui.MODE_NEW
import org.jetbrains.anko.AnkoLogger


class AddEditActivity : AppCompatActivity(), AddEditContract.View, View.OnClickListener, AnkoLogger {

    private lateinit var mPresenter: AddEditContract.Presenter
    private lateinit var mProduct: Product

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    private var mode = MODE_NEW
    private var bytes: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addedit_product)

        mPresenter = AddEditPresenter(this)

        if (intent.hasExtra(KEY_PRODUCT)) {
            mode = MODE_EDIT
            mProduct = intent.getParcelableExtra(KEY_PRODUCT)
            setProduct(mProduct)
            btnRegister.visibility = View.VISIBLE
        }

        initView()

        btnRegister.setOnClickListener(this)
        ivProduct.setOnClickListener(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val imageBitmap = data!!.extras.get("data") as Bitmap
                bytes = BitmapUtils.resizeWidth(imageBitmap, 960)
                Glide.with(this).load(bytes).into(ivProduct)
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                val uri = data!!.data
                bytes = BitmapUtils.resizeWidth(this, uri, 960)
                Glide.with(this).load(bytes).into(ivProduct)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnRegister -> {
                if (mode == MODE_NEW) {
                    if (bytes == null) {
                        mPresenter.saveProduct(etProductName.text.toString(), etPrice.text.toString())
                    } else {
                        mPresenter.saveProduct(etProductName.text.toString(), etPrice.text.toString(), bytes!!)
                    }
                } else {
                    if (bytes == null) {
                        mPresenter.editProduct(mProduct, etProductName.text.toString(), etPrice.text.toString())
                    } else {
                        mPresenter.editProduct(mProduct, etProductName.text.toString(), etPrice.text.toString(), bytes!!)
                    }
                }

            }
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

    private fun initView() {
        etProductName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    btnRegister.visibility = View.GONE
                    tvErrProductName.visibility = View.VISIBLE
                } else {
                    tvErrProductName.visibility = View.GONE

                    if (!TextUtils.isEmpty(etPrice.text.toString()) && etPrice.text.toString().toInt() >= 5000) {
                        btnRegister.visibility = View.VISIBLE
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        etPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    btnRegister.visibility = View.GONE
                    tvErrPrice.visibility = View.VISIBLE
                    tvErrPrice.setText(R.string.invalid_price)
                } else if (s.toString().toInt() < 5000) {
                    btnRegister.visibility = View.GONE
                    tvErrPrice.visibility = View.VISIBLE
                    tvErrPrice.setText(R.string.invalid_money_more_than_5000)
                } else {
                    tvErrPrice.visibility = View.GONE

                    if (!TextUtils.isEmpty(etProductName.text.toString())) {
                        btnRegister.visibility = View.VISIBLE
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun setProduct(product: Product) {
        etProductName.setText(product.name)
        etPrice.setText(product.price.toString())
        if (!TextUtils.isEmpty(product.imgFileName)) {
            val imgRef = ImageDataSource().getImageRef(product.imgFileName)
            GlideApp.with(this)
                    .load(imgRef)
                    .into(ivProduct)
        }
    }

    private fun openGetImageDialog() {
        val countries = listOf(getString(R.string.camera), getString(R.string.gallery))
        selector(getString(R.string.choose_get_image_method), countries) { _, i ->
            when (i) {
                0 -> getImageFromCamera()
                1 -> getImageFromGallery()
            }
        }
    }

    private fun getImageFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY)
    }

}