package com.youknow.hppk2018.angelswing.data.model

import android.os.Parcelable
import com.youknow.hppk2018.angelswing.ui.MINIMUM_TARGET_PRICE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product (
        var id: String = "",
        val name: String = "",
        val imgFileName: String = "",
        val price: Int = MINIMUM_TARGET_PRICE,
        val onSale: Boolean = true,
        val createdAt: Long = System.currentTimeMillis(),
        val seller: User = User()
) : Parcelable {

    init {
        id = "${seller.hpAccount.replace("\\.", "_")}_$createdAt"
    }
}