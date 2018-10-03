package com.youknow.hppk2018.angelswing.data.source

import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Single
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * Created by Aaron on 30/06/2017.
 */

class ImageDataSource : AnkoLogger {

    private val BUCKET_URL = "gs://hppk-2018-angel-s-wing.appspot.com"
    private val storage = FirebaseStorage.getInstance(BUCKET_URL)

    fun saveImage(imgData: ByteArray, filename: String): Single<Boolean> {
        val ref = storage.reference
        val imgRef = ref.child(filename)

        return Single.create<Boolean> { emitter ->
            imgRef.putBytes(imgData)
                    .addOnCompleteListener {
                        info("[HPPK] saveImage - complete: ${it.isSuccessful}")
                        emitter.onSuccess(it.isSuccessful)
                    }
        }

    }

    fun getImageRef(filename: String) = storage.reference.child(filename)

}
