package com.switchsolutions.farmtohome.bdo.requestmodels

import com.google.gson.annotations.SerializedName

class CreateBdoRequestModel(
    @SerializedName("customer_id")
    var customer_id: String? = null,

    @SerializedName("delivery_date")
    var delivery_date: String? = null,

    @SerializedName("city_id")
    var city_id: String? = null,

    @SerializedName("products")
    var products: ArrayList<Products>? = arrayListOf()
        )
