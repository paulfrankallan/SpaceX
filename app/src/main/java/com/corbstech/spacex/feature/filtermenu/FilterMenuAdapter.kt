package com.corbstech.spacex.feature.filtermenu

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

class FilterMenuAdapter(
    private val mContext: Context,
    var filterMenuState: FilterMenuState = FilterMenuState(),
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return filterMenuState.headerList.size
    }

    override fun getChildrenCount(groupPosition: Int) =
        filterMenuState.filterOptionMap[filterMenuState.headerList[groupPosition]]?.size ?: 0

    override fun getGroup(groupPosition: Int): Any {
        return filterMenuState.headerList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        return filterMenuState
            .filterOptionMap[filterMenuState.headerList[groupPosition]]?.get(childPosition)
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
    ): View? {
        var view = convertView
        if (view == null) {
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.filter_header, parent, false)
        }
        return view.apply {
            val headerName = view?.findViewById(R.id.header_title) as TextView
            headerName.setTypeface(null, Typeface.NORMAL)
            val header = getGroup(groupPosition) as FilterMenuItem
            headerName.text = header.itemName
        }
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var view = convertView
        if (view == null) {
            val inflater = this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.filter_option, parent, false)
        }
        return view?.apply {
            val childName = findViewById<TextView>(R.id.child_title)
            val childIcon = findViewById<ImageView>(R.id.child_icon)
            val childItem = getChild(groupPosition, childPosition) as FilterMenuItem
            childName.text = childItem.itemName
            if (childItem.selected) {
                childIcon.setImageResource(R.drawable.ic_check)
                childIcon.visibility = VISIBLE
            } else {
                childIcon.visibility = INVISIBLE
            }
        }
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    fun setData(filterMenuState: FilterMenuState) {
        this.filterMenuState = filterMenuState
        notifyDataSetChanged()
    }

    fun getFilterMenuItem(groupPosition: Int, childPosition: Int) =
        getChild(groupPosition, childPosition) as FilterMenuItem?
}