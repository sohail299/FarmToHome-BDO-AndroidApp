package com.switchsolutions.farmtohome.bdo.services

import android.content.Context
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.RetrofitApiManager
import com.switchsolutions.farmtohome.bdo.api.ApiCommService
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.responsemodels.CustomersResponseModel

class GetCustomersService(val context: Context,
                              val cityId : Int
) : RetrofitApiManager<CustomersResponseModel>(context) {

    init {
        callApi()
    }
    private fun callApi(){
        val api : ApiCommService = RestApiClient.getClient(addHeaders = true)
        callServer(api.getCustomersDetail(cityId = cityId))
    }

    override fun onSuccess(t: CustomersResponseModel?) {
        t?.status!!
        if (t != null){
            if (t.data.isNotEmpty())
                MainActivity.usersData = t.data
            //MainActivity.newInstance().updateCustomerDetails()
        }
    }
    override fun onFailure(t: ErrorDto) {
        t
    }

    override fun tokenRefreshed() {
        callApi()
    }
}


