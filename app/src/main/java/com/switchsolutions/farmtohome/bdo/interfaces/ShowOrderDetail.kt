package com.switchsolutions.farmtohome.bdo.interfaces

interface ShowOrderDetail {
    fun showOrderDetails(reqId: Int )
    fun startObserverForSingleOrder()
}