package com.corbstech.spacex.feature.list.company

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.corbstech.spacex.R
import com.corbstech.spacex.app.ui.list.AdapterListener
import com.corbstech.spacex.app.ui.list.ItemView
import com.corbstech.spacex.app.ui.list.RecyclerItem
import com.corbstech.spacex.databinding.ListItemCompanyBinding

object CompanyItemView : ItemView<RecyclerItem>() {

  override fun belongsTo(item: RecyclerItem?) = item is CompanyItem
  override fun type() = R.layout.list_item_company

  override fun holder(parent: ViewGroup): RecyclerView.ViewHolder {
    return CompanyItemViewHolder(
      ListItemCompanyBinding.inflate(
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
    if (holder is CompanyItemViewHolder && item is CompanyItem) {
      holder.bind(item)
    }
  }
}
