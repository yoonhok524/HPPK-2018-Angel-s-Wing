package com.youknow.hppk2018.angelswing.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
        val id: String = "",
        val name: String = "",
        val hpAccount: String = "",
        val lab: String = "",
        val part: String = ""
) : Parcelable