package com.youknow.hppk2018.angelswing.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.youknow.hppk2018.angelswing.data.model.Product
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.ui.FAVORITES
import com.youknow.hppk2018.angelswing.ui.USERS
import io.reactivex.Single
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class UserDataSource : AnkoLogger {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun saveUser(user: User) = Single.create<Boolean> { emitter ->
        mFirestore.collection(USERS)
                .document(user.id)
                .set(user)
                .addOnCompleteListener {
                    info("[HPPK] saveUser - complete: $user")
                    emitter.onSuccess(it.isSuccessful)
                }
    }

    fun getUser(id: String) = Single.create<User> { emitter ->
        mFirestore.collection(USERS)
                .document(id)
                .get()
                .addOnCompleteListener {
                    info("[HPPK] getUser($id) - complete: ${it.isSuccessful}")
                    if (it.isSuccessful) {
                        if (it.result != null && it.result.data != null) {
                            emitter.onSuccess(it.result.toObject(User::class.java)!!)
                        } else {
                            emitter.onError(Exception("User Not Found - $id"))
                        }
                    } else {
                        emitter.onError(Exception("User Not Found - $id"))
                    }
                }
    }

    fun getFavorites(id: String) = Single.create<List<Product>> { emitter ->
        mFirestore.collection(USERS)
                .document(id)
                .collection(FAVORITES)
                .get()
                .addOnCompleteListener {
                    info("[HPPK] getFavoriteNumber($id) - complete: ${it.isSuccessful}")
                    if (it.isSuccessful) {
                        if (it.result != null && it.result.documents != null) {
                            emitter.onSuccess(it.result.toObjects(Product::class.java))
                        } else {
                            emitter.onError(Exception("Favorites Not Found - $id"))
                        }
                    } else {
                        emitter.onError(Exception("Favorites Not Found - $id"))
                        if (it.exception != null) {
                            it.exception!!.printStackTrace()
                        }
                    }
                }
    }

    fun addFavoriteProduct(userId: String, product: Product) = Single.create<Boolean> { emitter ->
        mFirestore.collection(USERS)
                .document(userId)
                .collection(FAVORITES)
                .document(product.id)
                .set(product)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(true)
                    } else {
                        emitter.onError(Exception("addFavoriteProduct failed"))
                    }
                }
    }

    fun removeFavoriteProduct(userId: String, product: Product) = Single.create<Boolean> { emitter ->
        mFirestore.collection(USERS)
                .document(userId)
                .collection(FAVORITES)
                .document(product.id)
                .delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emitter.onSuccess(true)
                    } else {
                        emitter.onError(Exception("removeFavoriteProduct failed"))
                    }
                }
    }

}