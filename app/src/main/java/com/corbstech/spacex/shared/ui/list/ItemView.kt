package com.corbstech.spacex.shared.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class ItemView<T> {

  abstract fun belongsTo(item: T?): Boolean
  abstract fun type(): Int
  abstract fun holder(parent: ViewGroup): RecyclerView.ViewHolder
  abstract fun bind(holder: RecyclerView.ViewHolder, item: T?, listener: AdapterListener?)

  protected fun ViewGroup.viewOf(@LayoutRes resource: Int): View {
    return LayoutInflater
      .from(context)
      .inflate(resource, this, false)
  }
}
