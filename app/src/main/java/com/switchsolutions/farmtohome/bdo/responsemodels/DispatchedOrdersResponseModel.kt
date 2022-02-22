package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class DispatchedOrdersResponseModel(
    @SerializedName("statusCode") var statusCode: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data :  ArrayList<DispatchedOrdersData> = arrayListOf(),
)
