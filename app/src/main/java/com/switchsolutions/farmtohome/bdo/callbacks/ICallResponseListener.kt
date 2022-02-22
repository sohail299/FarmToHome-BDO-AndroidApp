package com.switchsolutions.farmtohome.bdo.callbacks


interface ICallResponseListener <T> {
        fun onSuccessResponse(t: T)
        fun onFailure(t: ErrorDto)

}