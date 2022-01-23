package com.corbstech.spacex.shared.ui.list

interface RecyclerItem {
  val id: Long
  override fun equals(other: Any?): Boolean
}
