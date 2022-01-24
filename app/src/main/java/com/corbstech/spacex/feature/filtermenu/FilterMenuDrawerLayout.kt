package com.corbstech.spacex.feature.filtermenu

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class FilterMenuDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : DrawerLayout(context, attrs, defStyle) {
    override fun close() {
        closeDrawer(GravityCompat.END)
    }
}