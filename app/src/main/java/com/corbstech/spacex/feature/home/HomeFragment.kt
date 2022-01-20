package com.corbstech.spacex.feature.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.corbstech.spacex.R
import com.corbstech.spacex.databinding.FragmentHomeBinding
import com.corbstech.spacex.feature.home.list.company.CompanyItemView
import com.corbstech.spacex.feature.home.list.header.HeaderItemView
import com.corbstech.spacex.feature.home.list.launch.LaunchItemView
import com.corbstech.spacex.shared.SpaceXViewModel
import com.corbstech.spacex.shared.ViewSate
import com.corbstech.spacex.shared.ui.list.ListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    // region Members

    private val spaceXViewModel: SpaceXViewModel by activityViewModels()
    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private val listAdapter by lazy {
        ListAdapter(
            listener = null,
            itemViewTypes = listOf(
                HeaderItemView,
                CompanyItemView,
                LaunchItemView
            )
        )
    }

    // endregion

    // region Lifecycle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentHomeBinding = FragmentHomeBinding.bind(view)
        initRecyclerView()
        collectState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentHomeBinding = null
    }

    // endregion

    // region Handle view state

    private fun collectState() {
        lifecycleScope.launchWhenStarted {
            spaceXViewModel.state.collect { state ->
                handleState(state)
            }
        }
    }

    private fun handleState(state: ViewSate) {
        listAdapter.submitList(state.getItems())
    }

    // endregion

    // region List

    private fun initRecyclerView() {
        with(fragmentHomeBinding) {
            this?.listRecyclerview?.apply {
                setHasFixedSize(true)
                addItemDecoration(
                    DividerItemDecoration(
                        this@HomeFragment.requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = listAdapter
            }
        }
    }

    // endregion
}