package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class CustomersData (
    @SerializedName("value") var value: Int? = null,
    @SerializedName("label") var label: String? = null
)
