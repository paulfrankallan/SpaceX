package com.corbstech.spacex.feature.list.launch

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.corbstech.spacex.app.framework.GlideApp
import com.corbstech.spacex.databinding.ListItemLaunchBinding

class LaunchItemViewHolder(private val binding: ListItemLaunchBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(launchItem: LaunchItem) {
        with(binding) {
            valueMission.text = launchItem.mission
            valueDateTime.text = launchItem.date
            valueRocket.text = launchItem.rocket
            labelDays.text = launchItem.daysLabelAndValue?.first
            valueDays.text = launchItem.daysLabelAndValue?.second
            when (launchItem.success) {
                true,
                false -> {
                    launchItem.successImage?.let { imageSuccess.setImageResource(it) }
                }
                else -> {
                    imageSuccess.setImageDrawable(null)
                }
            }
            GlideApp.with(itemView.context)
                .load(launchItem.patchImage)
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imagePatch)
        }
    }
}