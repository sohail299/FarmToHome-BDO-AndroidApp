package com.switchsolutions.farmtohome.bdo.responsemodels.pojo

import com.google.gson.annotations.SerializedName


data class Roles(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("guard") var guard: String? = null,
    @SerializedName("permissions") var permissions: ArrayList<String> = arrayListOf()

)