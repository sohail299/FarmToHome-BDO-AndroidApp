package com.switchsolutions.farmtohome.bdo.responsemodels.pojo

import com.google.gson.annotations.SerializedName


data class LoginResponseModel(

    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("accessToken") var accessToken: String? = null,
    @SerializedName("tokenType") var tokenType: String? = null,
    @SerializedName("expiresIn") var expiresIn: Int? = null

)