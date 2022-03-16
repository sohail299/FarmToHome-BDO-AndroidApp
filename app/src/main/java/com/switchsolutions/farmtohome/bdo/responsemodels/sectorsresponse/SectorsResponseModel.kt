package com.switchsolutions.farmtohome.bdo.responsemodels.sectorsresponse

data class SectorsResponseModel(
    val `data`: List<Data>,
    val message: String,
    val status: String,
    val statusCode: Int
)