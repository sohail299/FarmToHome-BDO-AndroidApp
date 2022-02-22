package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class DashBoardOrdersData (
    @SerializedName("customer_id") var customer_id: Int? = null,
    @SerializedName("customer") var customer: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("delivery_date") var delivery_date: String? = null,
    @SerializedName("status") var status :  Int? = null,
    @SerializedName("rejected_comments") var rejected_comments :  String? = null,
        )
