package com.youknow.hppk2018.angelswing.utils

import java.text.NumberFormat
import java.util.*

fun getFormattedPrice(price: Int) = NumberFormat.getCurrencyInstance(Locale.KOREA).format(price)

fun getSaleLocation(part: String) = when (part) {
    "파트-이근철" -> "A"
    "파트-이기창" -> "B"
    "파트-허남" -> "C"
    "파트-하우화" -> "D"
    "파트-조태균" -> "E"
    "파트-이중목" -> "F"
    "파트-김행난" -> "G"
    "파트-강형종" -> "H"
    "파트-채수경" -> "I"
    "파트-김수동" -> "J"
    "파트-도대회" -> "K"
    "파트-김병유" -> "L"
    "파트-권용찬" -> "M"
    "파트-Mohamad" -> "N"
    "파트-심인보" -> "O"
    "파트-System Architect" -> "P"
    "파트-최준영" -> "Q"
    else -> "All"
}