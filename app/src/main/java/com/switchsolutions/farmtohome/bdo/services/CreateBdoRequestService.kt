package com.switchsolutions.farmtohome.bdo.services

import android.content.Context
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.CartViewModel
import com.switchsolutions.farmtohome.bdo.MainActivity
import com.switchsolutions.farmtohome.bdo.RetrofitApiManager
import com.switchsolutions.farmtohome.bdo.api.ApiCommService
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.callbacks.ICallBackListener
import com.switchsolutions.farmtohome.bdo.callbacks.ICallResponseListener
import com.switchsolutions.farmtohome.bdo.fragments.CartFragment
import com.switchsolutions.farmtohome.bdo.requestmodels.CreateBdoRequestModel
import com.switchsolutions.farmtohome.bdo.responsemodels.BDORequestResponsemodel
import com.switchsolutions.farmtohome.bdo.responsemodels.GenericResponseModel
import org.json.JSONObject

class CreateBdoRequestService(val cartViewModel: CartViewModel,
                              val callback: ICallBackListener<BDORequestResponsemodel>,
                              val context: Context,
                              val requestModel : JsonObject) : RetrofitApiManager<BDORequestResponsemodel>(context) {
    init {
        callApi()
    }
     fun callApi(){
        val api : ApiCommService = RestApiClient.getClient(addHeaders = true)
        callServer(api.createBdoRequest(requestModel))
    }
    override fun onSuccess(t: BDORequestResponsemodel?) {
      t?.status!!
        //cartViewModel.clearAll()
        //MainActivity.newInstance().refreshFragment()
    }
    override fun onFailure(t: ErrorDto) {
        t
    }
    override fun tokenRefreshed() {
        callApi()
    }
}