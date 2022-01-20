package com.corbstech.spacex.shared.ui.list

interface RecyclerItem {
  val id: String?
  override fun equals(other: Any?): Boolean
}
