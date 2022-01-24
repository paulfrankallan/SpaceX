package com.corbstech.spacex.feature.list.launch

import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.corbstech.spacex.R
import com.corbstech.spacex.databinding.ListItemLaunchBinding
import com.corbstech.spacex.app.ui.list.AdapterListener
import com.corbstech.spacex.app.ui.list.ItemView
import com.corbstech.spacex.app.ui.list.RecyclerItem

object LaunchItemView : ItemView<RecyclerItem>() {
    private const val POPUP_MENU_TITLE = "Links"
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
            with(holder) {
                bind(item)
                itemView.setOnClickListener {
                    PopupMenu(itemView.context, itemView).apply {
                        menu.add(Menu.NONE, -1, 0, POPUP_MENU_TITLE).apply {
                            isEnabled = false
                        }
                        item.links.forEachIndexed { index, link ->
                            menu.add(Menu.NONE, index, index, link.title)
                        }
                        setOnMenuItemClickListener { menuItem ->
                            listener?.clickListener(item.links.first { it.title == menuItem.title })
                            true
                        }
                    }.show()
                }
            }
        }
    }
}
