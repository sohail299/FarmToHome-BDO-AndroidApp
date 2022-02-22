package com.switchsolutions.farmtohome.bdo.requestmodels

import com.google.gson.annotations.SerializedName

class Products(
    @SerializedName("name")
    var name : String? = null,

    @SerializedName("value")
    var value : String? = null,

    @SerializedName("is_removed")
    var is_removed : String? = null,
)
