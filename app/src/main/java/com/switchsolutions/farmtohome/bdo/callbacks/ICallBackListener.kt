package com.switchsolutions.farmtohome.bdo.callbacks


interface ICallBackListener<T> {
    fun onSuccess(t: T?)
    fun onFailure(t: ErrorDto)
}