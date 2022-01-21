package com.corbstech.spacex.feature.home

import android.content.Intent
import android.net.Uri
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
import com.corbstech.spacex.feature.home.list.launch.LaunchItemLink
import com.corbstech.spacex.feature.home.list.launch.LaunchItemView
import com.corbstech.spacex.shared.*
import com.corbstech.spacex.shared.ui.list.AdapterListener
import com.corbstech.spacex.shared.ui.list.ListAdapter
import com.corbstech.spacex.shared.ui.list.RecyclerItemClicked
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), AdapterListener {

    // region Members

    private val spaceXViewModel: SpaceXViewModel by activityViewModels()
    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private val listAdapter by lazy {
        ListAdapter(
            listener = this,
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
            spaceXViewModel.state.collect { state -> handleState(state) }
        }
    }

    private fun handleState(state: ViewSate) {
        listAdapter.submitList(state.getItems())
        state.events.forEach { event ->
            when (event) {
                is Event.LaunchWebBrowser -> {
                    launchWebBrowser(event)
                }
            }
        }
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

    override fun clickListener(item: RecyclerItemClicked) {
        when (item) {
            is LaunchItemLink -> {
                spaceXViewModel.dispatch(Action.LaunchItemLinkClicked(item))
            }
        }
    }

    // endregion

    private fun launchWebBrowser(launchWebBrowserEvent: Event.LaunchWebBrowser) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(launchWebBrowserEvent.url)))
        spaceXViewModel.removeConsumedEvent(launchWebBrowserEvent.uniqueId)
    }
}