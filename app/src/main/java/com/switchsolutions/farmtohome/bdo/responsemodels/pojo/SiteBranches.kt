package com.switchsolutions.farmtohome.bdo.responsemodels.pojo

import com.google.gson.annotations.SerializedName


data class SiteBranches (

  @SerializedName("value" ) var value : Int?    = null,
  @SerializedName("label" ) var label : String? = null,
  @SerializedName("code" ) var code : String? = null

)