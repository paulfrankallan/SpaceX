package com.corbstech.spacex.app.ui.list

interface RecyclerItem {
  val id: Long
  override fun equals(other: Any?): Boolean
}
