package com.youknow.hppk2018.angelswing.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.youknow.hppk2018.angelswing.data.model.User
import com.youknow.hppk2018.angelswing.ui.USERS
import io.reactivex.Single
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class UserDataSource : AnkoLogger {
    private val mFirestore = FirebaseFirestore.getInstance()

//    fun getPlayerByName(region: String, playerName: String): Single<Player> {
//        Log.d(TAG, "[BS] getPlayerByName(firebase)")
//        return Single.create<Player> { emitter ->
//            mFirestore.collection(REGIONS)
//                    .document(region)
//                    .collection(PLAYERS)
//                    .whereEqualTo("playerName", playerName)
//                    .get()
//                    .addOnCompleteListener {
//                        if (it.isSuccessful && !it.result.isEmpty) {
//                            emitter.onSuccess(it.result.documents[0].toObject(Player::class.java))
//                        } else {
//                            emitter.onError(PlayerNotExistException())
//                        }
//                    }
//        }
//    }

    fun saveUser(user: User) = Single.create<Boolean> { emitter ->
        mFirestore.collection(USERS)
                .document(user.id)
                .set(user)
                .addOnCompleteListener {
                    info("[HPPK] saveUser - complete: $user")
                    emitter.onSuccess(it.isSuccessful)
                }
    }

}