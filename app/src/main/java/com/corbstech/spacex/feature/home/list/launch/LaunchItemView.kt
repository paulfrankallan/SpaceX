package com.corbstech.spacex.feature.home.list.launch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.corbstech.spacex.R
import com.corbstech.spacex.databinding.ListItemLaunchBinding
import com.corbstech.spacex.shared.ui.list.AdapterListener
import com.corbstech.spacex.shared.ui.list.ItemView
import com.corbstech.spacex.shared.ui.list.RecyclerItem

object LaunchItemView : ItemView<RecyclerItem>() {

  override fun belongsTo(item: RecyclerItem?) = item is LaunchItem
  override fun type() = R.layout.list_item_launch

  override fun holder(parent: ViewGroup): RecyclerView.ViewHolder {
    return LaunchItemViewHolder(
      ListItemLaunchBinding.inflate(
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
    if (holder is LaunchItemViewHolder && item is LaunchItem) {
      holder.bind(item)
    }
  }
}
