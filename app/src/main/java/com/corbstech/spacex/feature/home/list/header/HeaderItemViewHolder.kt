package com.corbstech.spacex.feature.home.list.header

import androidx.recyclerview.widget.RecyclerView
import com.corbstech.spacex.databinding.ListItemHeaderBinding

class HeaderItemViewHolder(private val binding: ListItemHeaderBinding)
  : RecyclerView.ViewHolder(binding.root) {

  fun bind(headerItem: HeaderItem) {
    binding.title.text = headerItem.title
  }
}