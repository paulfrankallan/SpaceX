package com.corbstech.spacex.feature.list.company

import androidx.recyclerview.widget.RecyclerView
import com.corbstech.spacex.databinding.ListItemCompanyBinding

class CompanyItemViewHolder(private val binding: ListItemCompanyBinding)
  : RecyclerView.ViewHolder(binding.root) {
  fun bind(companyItem: CompanyItem) {
    binding.companyInfo.text = companyItem.info
  }
}