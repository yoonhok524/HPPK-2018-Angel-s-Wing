package com.youknow.hppk2018.angelswing.ui.addedit

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.youknow.hppk2018.angelswing.R
import com.youknow.hppk2018.angelswing.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_addedit_product.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import com.bumptech.glide.Glide
import org.jetbrains.anko.AnkoLogger


class AddEditActivity : AppCompatActivity(), AddEditContract.View, View.OnClickListener, AnkoLogger {

    private lateinit var mPresenter: AddEditContract.Presenter

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_GALLERY = 2

    var bytes: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addedit_product)

        mPresenter = AddEditPresenter(this)

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
                bytes = BitmapUtils.resizeWidth(imageBitmap, 600)
                Glide.with(this).load(bytes).into(ivProduct)
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                val uri = data!!.data
                bytes = BitmapUtils.resizeWidth(this, uri, 600)
                Glide.with(this).load(bytes).into(ivProduct)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnRegister -> {
                if (bytes == null) {
                    mPresenter.saveProduct(etProductName.text.toString(), tvPrice.text.toString())
                } else {
                    mPresenter.saveProduct(etProductName.text.toString(), tvPrice.text.toString(), bytes!!)
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

    override fun showInvalidName(visible: Int) {
        tvErrProductName.visibility = visible
    }

    override fun showInvalidPrice(visible: Int, msg: Int) {
        tvErrPrice.visibility = visible
        tvErrPrice.setText(msg)
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