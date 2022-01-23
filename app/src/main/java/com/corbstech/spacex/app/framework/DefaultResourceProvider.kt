package com.corbstech.spacex.app.framework

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

class DefaultResourceProvider @Inject constructor(val context: Context): ResourceProvider {
  override fun getResource(@StringRes resId: Int) = context.getString(resId)
  override fun getResource(@StringRes resId: Int, vararg format: Any?) = context.getString(resId, *format)
}