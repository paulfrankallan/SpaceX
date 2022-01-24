package com.corbstech.spacex.feature.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.corbstech.spacex.R
import com.corbstech.spacex.app.Action
import com.corbstech.spacex.app.Event
import com.corbstech.spacex.app.SpaceXViewModel
import com.corbstech.spacex.app.ViewSate
import com.corbstech.spacex.app.ui.list.AdapterListener
import com.corbstech.spacex.app.ui.list.ListAdapter
import com.corbstech.spacex.app.ui.list.RecyclerItemClicked
import com.corbstech.spacex.databinding.FragmentSpacexListBinding
import com.corbstech.spacex.feature.list.company.CompanyItemView
import com.corbstech.spacex.feature.list.header.HeaderItemView
import com.corbstech.spacex.feature.list.launch.LaunchItemLink
import com.corbstech.spacex.feature.list.launch.LaunchItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.*

@AndroidEntryPoint
class SpaceXListFragment : Fragment(R.layout.fragment_spacex_list), AdapterListener {

    // region Members

    private val spaceXViewModel: SpaceXViewModel by activityViewModels()
    private var fragmentHomeBinding: FragmentSpacexListBinding? = null
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
        fragmentHomeBinding = FragmentSpacexListBinding.bind(view)
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
        handleEvents(state.events)
        handleNoData(state)
        updateProgressSpinner(state.refreshing)
    }

    private fun handleEvents(events: List<Event>) {
        events.forEach { event ->
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
                        this@SpaceXListFragment.requireContext(),
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
                spaceXViewModel.dispatch(Action.LaunchItemLinkClicked(launchItemLink = item))
            }
        }
    }

    // endregion

    // region Web links

    private fun launchWebBrowser(launchWebBrowserEvent: Event.LaunchWebBrowser) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(launchWebBrowserEvent.url)))
        spaceXViewModel.removeConsumedEvent(launchWebBrowserEvent.uniqueId)
    }

    // endregion

    // region Refresh spinner

    private fun updateProgressSpinner(refreshing: Boolean) {
        when (refreshing) {
            true -> {
                savingStartTime = Date().time
                fragmentHomeBinding?.progressSpinner?.setImageDrawable(
                    CircularProgressDrawable(requireContext()).apply {
                        setColorSchemeColors(
                            *listOf(
                                ContextCompat.getColor(requireContext(), R.color.white),
                                ContextCompat.getColor(requireContext(), R.color.black)
                            ).toIntArray()
                        )
                        setStyle(CircularProgressDrawable.LARGE)
                        start()
                    })
                fragmentHomeBinding?.progressSpinner?.visibility = View.VISIBLE
            }
            false -> {
                lifecycleScope.launchWhenStarted {
                    hideSpinner()
                }
            }
        }
    }

    private var savingStartTime = 0L
    private val delayMillis = 1300L

    private suspend fun hideSpinner() {
        // Ensure refreshing spinner shown for a minimum [delayMillis] duration.
        val currentTimeMillis = Date().time
        val elapsedTimeMillis = currentTimeMillis - savingStartTime
        val remainingTimeMillis = delayMillis - elapsedTimeMillis
        if (elapsedTimeMillis > delayMillis) {
            fragmentHomeBinding?.progressSpinner?.visibility = View.GONE
        } else {
            delay(remainingTimeMillis)
            fragmentHomeBinding?.progressSpinner?.visibility = View.GONE
        }
    }

    // endregion

    // region No data

    private fun handleNoData(state: ViewSate) = state.apply {
        fragmentHomeBinding?.listRecyclerview?.isVisible = !noData
        fragmentHomeBinding?.noContentLayout?.isVisible = noData
    }

    // endregion
}