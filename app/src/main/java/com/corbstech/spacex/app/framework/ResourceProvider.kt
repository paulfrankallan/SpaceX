package com.corbstech.spacex.app.framework

import androidx.annotation.StringRes

interface ResourceProvider {
  fun getResource(@StringRes resId: Int): String
  fun getResource(@StringRes resId: Int, vararg format: Any?): String
}