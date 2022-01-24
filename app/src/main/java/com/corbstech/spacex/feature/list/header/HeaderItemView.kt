package com.corbstech.spacex.feature.list.header

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.corbstech.spacex.R
import com.corbstech.spacex.app.ui.list.AdapterListener
import com.corbstech.spacex.app.ui.list.ItemView
import com.corbstech.spacex.app.ui.list.RecyclerItem
import com.corbstech.spacex.databinding.ListItemHeaderBinding

object HeaderItemView : ItemView<RecyclerItem>() {

  override fun belongsTo(item: RecyclerItem?) = item is HeaderItem
  override fun type() = R.layout.list_item_header

  override fun holder(parent: ViewGroup): RecyclerView.ViewHolder {
    return HeaderItemViewHolder(
      ListItemHeaderBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun bind(
    holder: RecyclerView.ViewHolder,
    item: RecyclerItem?,
    listener: AdapterListener?
  ) {
    if (holder is HeaderItemViewHolder && item is HeaderItem) {
      holder.bind(item)
    }
  }
}
