package com.switchsolutions.farmtohome.bdo.services

import android.content.Context
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.RetrofitApiManager
import com.switchsolutions.farmtohome.bdo.api.ApiCommService
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.responsemodels.CustomersResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.ProductsResponseModel

class GetProductsService(
    val context: Context,
    val cityId: Int
) : RetrofitApiManager<ProductsResponseModel>(context) {

    init {
        callApi()
    }

    private fun callApi() {
        val api: ApiCommService = RestApiClient.getClient(addHeaders = true)
        callServer(api.getProductsDetail(cityId = cityId))
    }

    override fun onSuccess(t: ProductsResponseModel?) {
        t?.status!!
        if (t != null) {
            if (t.data.isNotEmpty()) {
                MainActivity.productsData = t.data
            }
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