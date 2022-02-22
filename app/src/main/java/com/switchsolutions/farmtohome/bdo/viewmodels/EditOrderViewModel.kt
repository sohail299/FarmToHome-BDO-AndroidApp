package com.switchsolutions.farmtohome.bdo.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.DashboardResponseData
import com.switchsolutions.farmtohome.bdo.responsemodels.EditResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.GenericResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.RequisitionResponseModel
import timber.log.BuildConfig
import timber.log.Timber

class EditOrderViewModel : ViewModel() {


    var callEditApi: MutableLiveData<Boolean> = MutableLiveData()
    var apiResponseSuccess: MutableLiveData<EditResponseModel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()


    init {
        callEditApi.value = false
    }


    fun startObserver(editOrderObject: JsonObject) {
        callEditApi.value = true
        //call api here
        object : RetrofitApiManager<EditResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).editOrder(editOrderObject))
            }
            override fun onSuccess(t: EditResponseModel?) {
                callEditApi.value = false
                //saving user details
                apiResponseSuccess.value = t!!
            }

            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callEditApi.value = false
                apiResponseFailure.value = t
            }

            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }





}