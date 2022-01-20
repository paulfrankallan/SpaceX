package com.corbstech.spacex.shared.ui.list

class ItemViewTypes<T>(types: List<ItemView<T>>) {

  private val itemViewTypes: ArrayList<ItemView<T>> = ArrayList()

  init {
    types.forEach { addType(it) }
  }

  fun addType(type: ItemView<T>) {
    itemViewTypes.add(type)
  }

  fun of(item: T?): ItemView<T> {
    for (itemView in itemViewTypes) {
      if (itemView.belongsTo(item)) return itemView
    }
    throw NoSuchRecyclerItemTypeException(message = item?.toString() ?: "")
  }

  fun of(viewType: Int): ItemView<T> {
    for (itemView in itemViewTypes) {
      if (itemView.type() == viewType) return itemView
    }
    throw NoSuchRecyclerViewTypeException()
  }
}
