package com.switchsolutions.farmtohome.bdo.responsemodels.pojo

import com.google.gson.annotations.SerializedName


data class Data(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("roles") var roles: ArrayList<Roles> = arrayListOf(),
    @SerializedName("siteBranches") var siteBranches: ArrayList<SiteBranches> = arrayListOf()

)