package com.switchsolutions.farmtohome.bdo.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.fragments.CartFragment
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.BDORequestResponsemodel
import com.switchsolutions.farmtohome.bdo.responsemodels.DashboardResponseData
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.GenericResponseModel
import timber.log.BuildConfig
import timber.log.Timber

class SubmitCartViewModel : ViewModel() {
    var callCartSubmitApi: MutableLiveData<Boolean> = MutableLiveData()
    var apiResponseSuccess: MutableLiveData<BDORequestResponsemodel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    init {
        callCartSubmitApi.value = false
    }


    fun startObserver() {
        callCartSubmitApi.value = true
        //call api here
        object : RetrofitApiManager<BDORequestResponsemodel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).createBdoRequest(CartFragment.placeOrderJson))
            }
            override fun onSuccess(t: BDORequestResponsemodel?) {
                callCartSubmitApi.value = false
                //saving user details
                apiResponseSuccess.value = t!!
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callCartSubmitApi.value = false
                apiResponseFailure.value = t
            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
}