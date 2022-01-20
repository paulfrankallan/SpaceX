package com.corbstech.spacex.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.corbstech.spacex.shared.Order
import com.corbstech.spacex.shared.SpaceXViewModel
import com.corbstech.spacex.shared.ViewSate
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuAdapter
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SpaceXActivity :
    AppCompatActivity(),
    ExpandableListView.OnGroupClickListener {

    // region Members

    private lateinit var binding: ActivitySpacexBinding
    private val spaceXViewModel: SpaceXViewModel by viewModels()
    private var viewGroup: View? = null

    // endregion

    // region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpacexBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            setSupportActionBar(appBarMain.toolbar)
            supportActionBar?.title = ""
            expandableFilterView.setOnGroupClickListener(this@SpaceXActivity)
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

    private fun renderFilterMenu(filterMenuData: FilterMenuData) {
        binding.expandableFilterView.setAdapter( // TODO PFA PULL OUT ADAPTER
            FilterMenuAdapter(this, filterMenuData)
        )
    }

    override fun onGroupClick(
        parent: ExpandableListView, // TODO PFA REMOVED ?
        v: View?,
        groupPosition: Int,
        id: Long
    ): Boolean {
        if (parent.isGroupExpanded(groupPosition)) {
            parent.collapseGroup(groupPosition)
        } else {
            parent.expandGroup(groupPosition)
            when (groupPosition) {
                0 -> {
                    binding.expandableFilterView.choiceMode = ExpandableListView.CHOICE_MODE_SINGLE
                    parent.setOnChildClickListener { nestedParent, view, nestedGroupPosition, childPosition, _ ->
                        view.isSelected = true
                        viewGroup?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        viewGroup = view
                        viewGroup?.setBackgroundColor(Color.parseColor("#333333"))
                        when (childPosition) {
                            FILTER_MENU_INDEX_SORT_ASC -> {
                                dispatch(Action.Sort(Order.ASC))
                            }
                            FILTER_MENU_INDEX_SORT_DESC -> {
                                dispatch(Action.Sort(Order.DESC))
                            }
                        }
                        nestedParent.expandGroup(nestedGroupPosition)
                    }
                }
                1 -> {
                    binding.expandableFilterView.choiceMode =
                        ExpandableListView.CHOICE_MODE_MULTIPLE
                    parent.setOnChildClickListener { nestedParent, view, nestedGroupPosition, childPosition, _ ->
                        view.isSelected = true;
                        //viewGroup?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        viewGroup = view
                        viewGroup?.setBackgroundColor(Color.parseColor("#333333"))
                        when (childPosition) {
                            0 -> Log.d("PFA", "2.1")
                            1 -> Log.d("PFA", "2.2")
                            2 -> Log.d("PFA", "2.3")
                        }
                        nestedParent.expandGroup(nestedGroupPosition)
                    }
                }
                2 -> {
                    binding.expandableFilterView.choiceMode = ExpandableListView.CHOICE_MODE_SINGLE
                    parent.setOnChildClickListener { nestedParent, view, nestedGroupPosition, childPosition, _ ->
                        view.isSelected = true;
                        viewGroup?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        viewGroup = view
                        viewGroup?.setBackgroundColor(Color.parseColor("#333333"))
                        when (childPosition) {
                            0 -> Log.d("PFA", "3.1")
                            1 -> Log.d("PFA", "3.2")
                        }
                        nestedParent.expandGroup(nestedGroupPosition)
                    }
                }
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    // endregion

    private fun dispatch(action: Action) {
        lifecycleScope.launch {
            spaceXViewModel.actionDispatcher.emit(
                action
            )
        }
    }

    companion object {
        private const val FILTER_MENU_INDEX_SORT_ASC = 0
        private const val FILTER_MENU_INDEX_SORT_DESC = 1
    }
}