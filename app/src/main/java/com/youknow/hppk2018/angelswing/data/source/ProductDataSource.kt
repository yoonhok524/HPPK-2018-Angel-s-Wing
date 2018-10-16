package com.youknow.hppk2018.angelswing.data.source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.youknow.hppk2018.angelswing.data.exceptions.*
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.ui.FAVORITES
import com.youknow.hppk2018.angelswing.ui.PRODUCTS
import io.reactivex.Single
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class ProductDataSource : AnkoLogger {
    private val mRef = FirebaseFirestore.getInstance().collection(PRODUCTS)

    fun getAllProducts() = Single.create<List<Product>> { emitter ->
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
                        emitter.onError(Exception("getChartData failed"))
                    }
                }
    }

    fun getProducts(from: DocumentSnapshot? = null) = Single.create<List<DocumentSnapshot>> { emitter ->
        val query = if (from == null) {
            mRef.orderBy("createdAt", Query.Direction.DESCENDING).limit(20)
        } else {
            mRef.orderBy("createdAt", Query.Direction.DESCENDING).startAfter(from).limit(20)
        }

        query.get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.isEmpty) {
                            emitter.onSuccess(listOf())
                        } else {
                            emitter.onSuccess(it.result.documents)
                        }
                    } else if (it.exception != null) {
                        emitter.onError(it.exception!!)
                    } else {
                        emitter.onError(Exception("getChartData failed"))
                    }
                }
    }

    fun getProduct(productId: String) = Single.create<Product> { emitter ->
        mRef.document(productId)
                .get()
                .addOnCompleteListener {
                    if (!it.isSuccessful || it.result == null || !it.result.exists()) {
                        emitter.onError(Exception("$productId is not exist"))
                    } else {
                        emitter.onSuccess(it.result.toObject(Product::class.java)!!)
                    }
                }
    }

    fun saveProduct(product: Product) = Single.create<Product> { emitter ->
        mRef.document(product.id)
                .set(product)
                .addOnSuccessListener { emitter.onSuccess(product) }
                .addOnFailureListener { emitter.onError(ProductSaveFailureException(it)) }
                .addOnCanceledListener { emitter.onError(ProductSaveCanceledException("Storing a product has been canceled.")) }
    }

    fun delete(productId: String) = Single.create<String> { emitter ->
        mRef.document(productId)
                .delete()
                .addOnSuccessListener { emitter.onSuccess(productId) }
                .addOnFailureListener { emitter.onError(ProductDeleteFailureException(it)) }
                .addOnCanceledListener { emitter.onError(ProductDeleteCanceledException("Deleting a product has been canceled.")) }
    }

    fun getFavoriteNumber(productId: String) = Single.create<Int> { emitter ->
        mRef.document(productId)
                .collection(FAVORITES)
                .get()
                .addOnCompleteListener {
                    info("[HPPK] getFavoriteNumber($productId) - complete: ${it.isSuccessful}")
                    if (it.isSuccessful) {
                        if (it.result != null && it.result.documents != null) {
                            emitter.onSuccess(it.result.documents.size)
                        } else {
                            emitter.onSuccess(0)
                        }
                    } else {
                        emitter.onSuccess(0)
                        if (it.exception != null) {
                            it.exception!!.printStackTrace()
                        }
                    }
                }
    }

    fun addFavoriteUser(productId: String, userId: String) = Single.create<String> { emitter ->
        mRef.document(productId)
                .collection(FAVORITES)
                .document(userId)
                .set(mapOf(Pair(userId, userId)))
                .addOnSuccessListener { emitter.onSuccess(productId) }
                .addOnFailureListener { emitter.onError(FavoriteSaveFailureException(it)) }
                .addOnCanceledListener { emitter.onError(FavoriteSaveCanceledException("addFavoriteUser canceled: $productId")) }
    }

    fun removeFavoriteUser(productId: String, userId: String) = Single.create<String> { emitter ->
        mRef.document(productId)
                .collection(FAVORITES)
                .document(userId)
                .delete()
                .addOnSuccessListener { emitter.onSuccess(productId) }
                .addOnFailureListener { emitter.onError(FavoriteSaveFailureException(it)) }
                .addOnCanceledListener { emitter.onError(FavoriteSaveCanceledException("removeFavoriteuser canceled: $productId")) }
    }

}