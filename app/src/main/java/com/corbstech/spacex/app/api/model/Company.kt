package com.corbstech.spacex.app.api.model

import com.google.gson.annotations.SerializedName

data class Company(
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
)