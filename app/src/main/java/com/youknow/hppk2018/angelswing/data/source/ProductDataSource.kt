package com.youknow.hppk2018.angelswing.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.ui.PRODUCTS
import io.reactivex.Single
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ProductDataSource : AnkoLogger {
    private val mRef = FirebaseFirestore.getInstance().collection(PRODUCTS)

    fun getProducts() = Single.create<List<Product>> { emitter ->
        mRef.orderBy("createdAt", Query.Direction.DESCENDING)
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

    fun getProduct(productId: String) = Single.create<Product> { emitter ->
        mRef.document(productId)
                .get()
                .addOnCompleteListener {
                    emitter.onSuccess(it.result.toObject(Product::class.java)!!)
                }
    }

    fun saveProduct(product: Product) = Single.create<Boolean> { emitter ->
        mRef.document(product.id)
                .set(product)
                .addOnCompleteListener {
                    info("[HPPK] saveProduct - complete: $product")
                    emitter.onSuccess(it.isSuccessful)
                }
    }

    fun delete(product: Product) = Single.create<Boolean> { emitter ->
        mRef.document(product.id)
                .delete()
                .addOnCompleteListener {
                    emitter.onSuccess(it.isSuccessful)
                }
    }


}