package com.corbstech.spacex.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat.END
import androidx.lifecycle.lifecycleScope
import com.corbstech.spacex.R
import com.corbstech.spacex.databinding.ActivitySpacexBinding
import com.corbstech.spacex.shared.Action
import com.corbstech.spacex.shared.SpaceXViewModel
import com.corbstech.spacex.shared.ViewSate
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuAdapter
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuData
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class SpaceXActivity :
    AppCompatActivity(),
    ExpandableListView.OnGroupClickListener {

    // region Members

    private lateinit var binding: ActivitySpacexBinding
    private val spaceXViewModel: SpaceXViewModel by viewModels()
    private lateinit var filterMenuAdapter: FilterMenuAdapter
    private var filterIcon: Int = R.drawable.ic_filter_outline

    // endregion

    // region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpacexBinding.inflate(layoutInflater)
        filterMenuAdapter = FilterMenuAdapter(this)
        with(binding) {
            setContentView(root)
            setSupportActionBar(appBarMain.toolbar)
            supportActionBar?.title = ""
            expandableFilterView.setOnGroupClickListener(this@SpaceXActivity)
            expandableFilterView.setAdapter(filterMenuAdapter)
        }
        collectState()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(END)) {
            binding.drawerLayout.closeDrawer(END)
        } else {
            return super.onBackPressed()
        }
    }

    // endregion

    // region Handle State

    private fun collectState() {
        lifecycleScope.launchWhenStarted {
            spaceXViewModel.state.collect { state ->
                handleState(state)
            }
        }
    }

    private fun handleState(state: ViewSate) {
        renderFilterMenu(state.filterMenuData)
        updateFilterIcon(state.filterMenuData.filtersApplied)
    }

    private fun updateFilterIcon(filtersApplied: Boolean) {
        if (filtersApplied) {
            filterIcon = R.drawable.ic_filter_check_outline
        } else {
            filterIcon = R.drawable.ic_filter_outline
        }
        invalidateOptionsMenu()
    }

    // endregion

    // region Filter menu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                if (binding.drawerLayout.isDrawerOpen(END)) {
                    binding.drawerLayout.closeDrawer(END)
                } else {
                    binding.drawerLayout.openDrawer(END)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val settingsItem = menu.findItem(R.id.action_filter)
        settingsItem.icon = ContextCompat.getDrawable(this, filterIcon)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun renderFilterMenu(filterMenuData: FilterMenuData) {
        filterMenuAdapter.setData(filterMenuData)
    }

    override fun onGroupClick(
        parent: ExpandableListView,
        v: View?,
        groupPosition: Int,
        id: Long
    ): Boolean {
        if (parent.isGroupExpanded(groupPosition)) {
            parent.collapseGroup(groupPosition)
        } else {
            parent.expandGroup(groupPosition)
            parent.setOnChildClickListener { nestedParent, _, nestedGroupPosition, childPosition, _ ->
                filterMenuAdapter.getFilterMenuItem(
                    groupPosition = nestedGroupPosition,
                    childPosition = childPosition
                )?.let {
                    spaceXViewModel.dispatch(
                        Action.FilterMenuItemClicked(
                            filterMenuGroup = filterMenuAdapter
                                .filterMenuData.headerList[nestedGroupPosition],
                            filterMenuItem = it
                        )
                    )
                }
                nestedParent.expandGroup(nestedGroupPosition)
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    // endregion
}