package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class OrderProductsData(
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("unit") var unit: String? = null,
    @SerializedName("is_removed") var is_removed: String? = null,
    @SerializedName("value") var value: Int? = null,

    )
