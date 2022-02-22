package com.switchsolutions.farmtohome.bdo.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.fragments.DispatchFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.*
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import timber.log.BuildConfig
import timber.log.Timber

class DispatchViewModel : ViewModel() {

    var callSignInApi: MutableLiveData<Boolean> = MutableLiveData()

    var apiResponseSuccess: MutableLiveData<DispatchedOrdersResponseModel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    var apiDeliveredSuccess: MutableLiveData<DeliveryStatusResponseModel> = MutableLiveData()
    var apiDeliveredFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    init {
        callSignInApi.value = false
    }
    fun startObserver() {
        callSignInApi.value = true
        //call api here
        object : RetrofitApiManager<DispatchedOrdersResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).getDispatchedOrders(DispatchFragment.dispatchedOrdersJson))
            }
            override fun onSuccess(t: DispatchedOrdersResponseModel?) {
                callSignInApi.value = false
                //saving user details
                apiResponseSuccess.value = t!!
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callSignInApi.value = false
                apiResponseFailure.value = t
            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
    fun markAsDelivered(reqObj: JsonObject){

        object : RetrofitApiManager<DeliveryStatusResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).markAsDelivered(reqObj))
            }
            override fun onSuccess(t: DeliveryStatusResponseModel?) {
                callSignInApi.value = false
                //saving user details
                apiDeliveredSuccess.value = t!!
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callSignInApi.value = false
                apiDeliveredFailure.value = t
            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
}