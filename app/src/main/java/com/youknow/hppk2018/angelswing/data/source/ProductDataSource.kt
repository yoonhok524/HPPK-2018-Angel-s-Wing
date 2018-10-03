package com.youknow.hppk2018.angelswing.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.ui.PRODUCTS
import io.reactivex.Single
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ProductDataSource : AnkoLogger {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun getProducts() = Single.create<List<Product>> { emitter ->
        mFirestore.collection(PRODUCTS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.isEmpty) {
                            emitter.onSuccess(listOf())
                        } else {
                            emitter.onSuccess(it.result.toObjects(Product::class.java))
                        }
                    } else if (it.exception != null) {
                        emitter.onError(it.exception!!)
                    } else {
                        emitter.onError(Exception("getProducts failed"))
                    }
                }
    }

    fun saveProduct(product: Product) = Single.create<Boolean> { emitter ->
        mFirestore.collection(PRODUCTS)
                .document()
                .set(product)
                .addOnCompleteListener {
                    info("[HPPK] saveProduct - complete: $product")
                    emitter.onSuccess(it.isSuccessful)
                }
    }

}