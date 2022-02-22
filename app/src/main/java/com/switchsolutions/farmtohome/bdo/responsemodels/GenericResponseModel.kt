package com.switchsolutions.farmtohome.bdo.responsemodels

data class GenericResponseModel<T> (
        val msg : String,
        val errorCode: Int,
        val result : T
)