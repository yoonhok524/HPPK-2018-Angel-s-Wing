package com.youknow.hppk2018.angelswing.data.exceptions

import java.lang.Exception

class FavoriteSaveCanceledException(msg: String) : Exception(msg)

class FavoriteSaveFailureException(e: Exception) : Exception(e)

class FavoriteDeleteCanceledException(msg: String) : Exception(msg)

class FavoriteDeleteFailureException(e: Exception) : Exception(e)

