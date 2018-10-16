package com.youknow.hppk2018.angelswing.data.exceptions

import java.lang.Exception

class ProductSaveCanceledException(msg: String) : Exception(msg)

class ProductSaveFailureException(e: Exception) : Exception(e)

class ProductDeleteCanceledException(msg: String) : Exception(msg)

class ProductDeleteFailureException(e: Exception) : Exception(e)

