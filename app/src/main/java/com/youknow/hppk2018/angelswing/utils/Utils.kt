package com.youknow.hppk2018.angelswing.utils

import java.text.NumberFormat
import java.util.*

fun getFormattedPrice(price: Int) = NumberFormat.getCurrencyInstance(Locale.KOREA).format(price)