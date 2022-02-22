package com.switchsolutions.farmtohome.bdo.services

import android.content.Context
import com.google.gson.Gson
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.api.ApiCommService
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.callbacks.IOnTokenRefreshed
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.GenericResponseModel

class RefreshTokenService(val context : Context, private val tokenRefreshCallBack: IOnTokenRefreshed) : RetrofitApiManager<GenericResponseModel<LoginResponseModel>>(context) {

    fun refreshToken(email : String){
        val api : ApiCommService = RestApiClient.getClient(addHeaders = false)
        callServer(api.refreshToken(email))
    }

    override fun onSuccess(t: GenericResponseModel<LoginResponseModel>?) {
        if (t?.result != null) {
            val sharedPref = SharedPrefUtils.getInstance(AppController.ApplicationContext)
            sharedPref.setValue(SharedPrefUtils.USER_PROFILE, Gson().toJson(t.result))
        }
        tokenRefreshCallBack.onTokenRefreshed()
    }

    override fun onFailure(t: ErrorDto) {
        //TODO: Refresh again
    }

    override fun tokenRefreshed() {
        //nothing required in this case
    }

}