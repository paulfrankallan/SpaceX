package com.corbstech.spacex.app.ui.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(
  itemViewTypes: List<ItemView<RecyclerItem>>,
  private val listener: AdapterListener? = null
) : ListAdapter<RecyclerItem, RecyclerView.ViewHolder>(BASE_DIFF_CALLBACK) {

  init {
      setHasStableIds(true)
  }

  private val viewTypes: ItemViewTypes<RecyclerItem> = ItemViewTypes(itemViewTypes)

  override fun getItemViewType(position: Int) = viewTypes.of(getItem(position)).type()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    viewTypes.of(viewType).holder(parent)

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
    getItem(position).let { viewTypes.of(it).bind(holder, it, listener) }

  override fun getItemId(position: Int) = getItem(position).id
}
