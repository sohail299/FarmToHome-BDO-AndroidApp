package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class ProductsData (
    @SerializedName("value") var value: Int? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("unit") var unit: String? = null
)
