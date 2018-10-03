package com.youknow.hppk2018.angelswing.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.IOException


object BitmapUtils {

    fun resizeWidth(context: Context, uri: Uri, width: Int): ByteArray {

        var result: Bitmap? = null
        try {
            var originBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
            val filepath = getImageFilePath(context, uri)
            val imageDegree = getImageDegree(filepath)
            if (imageDegree != 0f) {
                originBitmap = rotateImage(originBitmap, imageDegree)
            }

            val aspectRatio = originBitmap.height.toDouble() / originBitmap.width.toDouble()
            val targetHeight = (width * aspectRatio).toInt()
            result = Bitmap.createScaledBitmap(originBitmap, width, targetHeight, false)
            if (result != originBitmap) {
                originBitmap.recycle()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val stream = ByteArrayOutputStream()
        result!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        result.recycle()
        return stream.toByteArray()
    }

    private fun getImageFilePath(context: Context, uri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.getContentResolver().query(uri, proj, null, null, null)
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                val column_index = cursor!!.getColumnIndexOrThrow(proj[0])
                result = cursor!!.getString(column_index)
            }
            cursor!!.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    private fun getImageDegree(filepath: String): Float {
        var degree = 0f
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(filepath)
            val exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270f
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    fun resizeWidth(originBitmap: Bitmap, width: Int): ByteArray {

        val aspectRatio = originBitmap.height.toDouble() / originBitmap.width.toDouble()
        val targetHeight = (width * aspectRatio).toInt()
        val result = Bitmap.createScaledBitmap(originBitmap, width, targetHeight, false)
        if (result != originBitmap) {
            originBitmap.recycle()
        }

        val stream = ByteArrayOutputStream()
        result.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        result.recycle()
        return stream.toByteArray()
    }

    fun rotateImage(src: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

}