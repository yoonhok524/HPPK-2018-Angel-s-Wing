package com.youknow.hppk2018.angelswing.data.model

import com.youknow.hppk2018.angelswing.ui.MINIMUM_TARGET_PRICE

data class Product (
        val name: String = "",
        val imgFileName: String = "",
        val price: Int = MINIMUM_TARGET_PRICE,
        val onSale: Boolean = true,
        val createdAt: Long = System.currentTimeMillis(),
        val seller: User = User()
)