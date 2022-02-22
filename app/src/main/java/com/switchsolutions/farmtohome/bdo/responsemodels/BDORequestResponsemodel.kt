package com.switchsolutions.farmtohome.bdo.responsemodels

import com.google.gson.annotations.SerializedName

data class BDORequestResponsemodel(
    @SerializedName("statusCode")
    var statusCode: Int? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("message")
    var message: String? = null

)