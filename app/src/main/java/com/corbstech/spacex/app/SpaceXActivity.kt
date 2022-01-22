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

@AndroidEntryPoint
class SpaceXActivity :
    AppCompatActivity(),
    ExpandableListView.OnGroupClickListener {

    // region Members

    private lateinit var binding: ActivitySpacexBinding
    private val spaceXViewModel: SpaceXViewModel by viewModels()
    private var viewGroup: View? = null
    private lateinit var filterMenuAdapter: FilterMenuAdapter

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
            when (groupPosition) {
                FILTER_MENU_GROUP_SORT -> {
                    binding.expandableFilterView.choiceMode = ExpandableListView.CHOICE_MODE_SINGLE
                    parent.setOnChildClickListener {
                            nestedParent, view, nestedGroupPosition, childPosition, _ ->
                        view.isSelected = true
                        filterMenuAdapter.getData().childList[
                                filterMenuAdapter.getData().headerList[groupPosition]
                        ]?.get(childPosition)?.let {
                            spaceXViewModel.dispatch(
                                Action.FilterMenuItemClicked(filterMenuItem = it)
                            )
                        }
                        nestedParent.expandGroup(nestedGroupPosition)
                    }
                }
            }
        }
        return true
    }

//    override fun onGroupClick(
//        parent: ExpandableListView,
//        v: View?,
//        groupPosition: Int,
//        id: Long
//    ): Boolean {
//        if (parent.isGroupExpanded(groupPosition)) {
//            parent.collapseGroup(groupPosition)
//        } else {
//            parent.expandGroup(groupPosition)
//            when (groupPosition) {
//                FILTER_MENU_GROUP_SORT -> {
//                    binding.expandableFilterView.choiceMode = ExpandableListView.CHOICE_MODE_SINGLE
//                    parent.setOnChildClickListener { nestedParent, view, nestedGroupPosition, childPosition, _ ->
//                        view.isSelected = true
//                        viewGroup?.setBackgroundColor(Color.parseColor("#FFFFFF"))
//                        viewGroup = view
//                        viewGroup?.setBackgroundColor(Color.parseColor("#333333"))
//                        when (childPosition) {
//                            FILTER_MENU_INDEX_SORT_ASC -> {
//                                spaceXViewModel.dispatch(Action.Sort(Order.ASC))
//                            }
//                            FILTER_MENU_INDEX_SORT_DESC -> {
//                                spaceXViewModel.dispatch(Action.Sort(Order.DESC))
//                            }
//                        }
//                        nestedParent.expandGroup(nestedGroupPosition)
//                    }
//                }
//                FILTER_MENU_GROUP_YEARS -> {
//                    binding.expandableFilterView.choiceMode =
//                        ExpandableListView.CHOICE_MODE_MULTIPLE
//                    parent.setOnChildClickListener { nestedParent, view, nestedGroupPosition, childPosition, _ ->
//                        view.isSelected = true;
//                        //viewGroup?.setBackgroundColor(Color.parseColor("#FFFFFF"))
//                        viewGroup = view
//                        viewGroup?.setBackgroundColor(Color.parseColor("#333333"))
//                        when (childPosition) {
//                            0 -> Log.d("PFA", "2.1")
//                            1 -> Log.d("PFA", "2.2")
//                            2 -> Log.d("PFA", "2.3")
//                        }
//                        nestedParent.expandGroup(nestedGroupPosition)
//                    }
//                }
//                FILTER_MENU_GROUP_SUCCESS -> {
//                    binding.expandableFilterView.choiceMode = ExpandableListView.CHOICE_MODE_SINGLE
//                    parent.setOnChildClickListener { nestedParent, view, nestedGroupPosition, childPosition, _ ->
//                        view.isSelected = true;
//                        viewGroup?.setBackgroundColor(Color.parseColor("#FFFFFF"))
//                        viewGroup = view
//                        viewGroup?.setBackgroundColor(Color.parseColor("#333333"))
//                        when (childPosition) {
//                            0 -> Log.d("PFA", "3.1")
//                            1 -> Log.d("PFA", "3.2")
//                        }
//                        nestedParent.expandGroup(nestedGroupPosition)
//                    }
//                }
//            }
//        }
//        return true
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    // endregion

    companion object {
        // Sort
        private const val FILTER_MENU_INDEX_SORT_ASC = 0
        private const val FILTER_MENU_INDEX_SORT_DESC = 1
        // Groups
        private const val FILTER_MENU_GROUP_SORT = 0
        private const val FILTER_MENU_GROUP_YEARS= 1
        private const val FILTER_MENU_GROUP_SUCCESS = 2
    }
}