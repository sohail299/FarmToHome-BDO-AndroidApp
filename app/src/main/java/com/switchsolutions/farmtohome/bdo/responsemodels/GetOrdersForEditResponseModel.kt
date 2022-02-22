package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class GetOrdersForEditResponseModel(
    @SerializedName("statusCode") var statusCode: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data :  EditOrdersData,
    @SerializedName("products") var products :  ArrayList<OrderProductsData> = arrayListOf(),
)