package com.corbstech.spacex.shared.ui.filterdrawer

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.corbstech.spacex.R

// TODO - Tidy

class FilterMenuAdapter(
    private val mContext: Context,
    private var filterMenuData: FilterMenuData = FilterMenuData(),
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return filterMenuData.headerList.size
    }

    override fun getChildrenCount(groupPosition: Int) =
        filterMenuData.childList[filterMenuData.headerList[groupPosition]]?.size ?: 0

    override fun getGroup(groupPosition: Int): Any {
        return filterMenuData.headerList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String? {
        return filterMenuData
            .childList[filterMenuData.headerList[groupPosition]]?.get(childPosition)
            ?.itemName
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val header = getGroup(groupPosition) as FilterMenuItem
        if (view == null) {
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.filter_header, parent, false)
        }
        val headerName = view?.findViewById(R.id.header_title) as TextView
        headerName.setTypeface(null, Typeface.NORMAL)
        headerName.text = header.itemName
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val childText = getChild(groupPosition, childPosition) as String
        val childItem = getGroup(groupPosition) as FilterMenuItem
        if (view == null) {
            val inflater = this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.filter_child, parent, false)
        }
        val childName = view!!
            .findViewById(R.id.child_title) as TextView
        val childIcon = view
            .findViewById(R.id.child_icon) as ImageView
        childName.text = childText
        if(childItem.selected) {
            childIcon.setImageResource(R.drawable.ic_check)
            childIcon.visibility = VISIBLE
        } else {
            childIcon.visibility = INVISIBLE
        }
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun setData(filterMenuData: FilterMenuData) {
        this.filterMenuData = filterMenuData
        notifyDataSetChanged()
    }

    fun getData() = filterMenuData
}