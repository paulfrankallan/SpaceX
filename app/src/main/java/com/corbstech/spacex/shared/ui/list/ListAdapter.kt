package com.corbstech.spacex.shared.ui.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(
  itemViewTypes: List<ItemView<RecyclerItem>>,
  private val listener: AdapterListener? = null
) : ListAdapter<RecyclerItem, RecyclerView.ViewHolder>(BASE_DIFF_CALLBACK) {

  private val viewTypes: ItemViewTypes<RecyclerItem> = ItemViewTypes(itemViewTypes)

  override fun getItemViewType(position: Int): Int {
    val item = getItem(position)
    return viewTypes.of(item).type()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return viewTypes.of(viewType).holder(parent)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = getItem(position)
    viewTypes.of(item).bind(holder, item, listener)
  }
}
