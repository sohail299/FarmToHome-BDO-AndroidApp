package com.switchsolutions.farmtohome.bdo.services

import android.content.Context
import com.switchsolutions.farmtohome.bdo.RetrofitApiManager
import com.switchsolutions.farmtohome.bdo.api.ApiCommService
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.responsemodels.DashboardResponseData

class DashboardOrdersService(val context: Context,
                             private val city_id : Int,
                             val created_by: Int
) : RetrofitApiManager<DashboardResponseData>(context) {

    init {
        callApi()
    }
    private fun callApi(){
        val api : ApiCommService = RestApiClient.getClient(addHeaders = true)
        callServer(api.getBdoRequests(city_id, created_by))
    }


    override fun onSuccess(t: DashboardResponseData?) {
        t?.status!!
    }

    override fun onFailure(t: ErrorDto) {
        t
    }

    override fun tokenRefreshed() {
        callApi()
    }
}
