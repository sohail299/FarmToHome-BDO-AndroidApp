package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class DeliveredResponseData(
    @SerializedName("assignToID") var assignToID: Int? = null,
    @SerializedName("assignTo") var assignTo: String? = null,
    @SerializedName("userid") var userid: Int? = null,
    @SerializedName("created_by") var created_by: String? = null,
    @SerializedName("customer_id") var customer_id: Int? = null,
    @SerializedName("customer") var customer: String? = null,
    @SerializedName("customer_address") var customer_address: String? = null,
    @SerializedName("customer_sector") var customer_sector: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("delivery_date") var delivery_date: String? = null,
    @SerializedName("delivered_at") var delivered_at: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("rejected_comments") var rejected_comments: String? = null

)
