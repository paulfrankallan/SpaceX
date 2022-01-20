package com.corbstech.spacex.feature.home.data.model

import com.corbstech.spacex.shared.ui.list.RecyclerItem
import com.google.gson.annotations.SerializedName

data class Company(
    override val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("founder")
    val founder: String = "",
    @SerializedName("founded")
    val founded: Int? = null,
    @SerializedName("employees")
    val employees: Int? = null,
    @SerializedName("launch_sites")
    val launchSites: Int? = null,
    @SerializedName("valuation")
    val valuation: Long? = null
) : RecyclerItem