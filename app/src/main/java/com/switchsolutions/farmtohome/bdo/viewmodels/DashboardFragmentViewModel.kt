package com.switchsolutions.farmtohome.bdo.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.DashboardResponseData
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.GenericResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.GetOrdersForEditResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.RequisitionResponseModel
import timber.log.BuildConfig
import timber.log.Timber

class DashboardFragmentViewModel : ViewModel() {


    var callSignInApi: MutableLiveData<Boolean> = MutableLiveData()
    var callSingleApi: MutableLiveData<Boolean> = MutableLiveData()
    var callDeleteApi: MutableLiveData<Boolean> = MutableLiveData()
    var apiResponseSuccess: MutableLiveData<DashboardResponseData> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    var singleApiResponseSuccess: MutableLiveData<GetOrdersForEditResponseModel> = MutableLiveData()
    var singleApiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    var apiRequisitionSuccess: MutableLiveData<RequisitionResponseModel> = MutableLiveData()
    var apiRequisitionFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    init {
        callSignInApi.value = false
    }

    fun startObserver() {
        callSignInApi.value = true
        //call api here
        object : RetrofitApiManager<DashboardResponseData>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).getBdoRequests(DashboardFragment.USER_STORED_CITY_ID, DashboardFragment.USER_ID))
            }
            override fun onSuccess(t: DashboardResponseData?) {
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

    fun getSingleOrder(reqID: Int) {
        callSingleApi.value = true
        //call api here
        object : RetrofitApiManager<GetOrdersForEditResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).getSingleOrder(reqId = reqID))
            }
            override fun onSuccess(t: GetOrdersForEditResponseModel?) {
                callSingleApi.value = false
                //saving user details
                singleApiResponseSuccess.value = t!!
            }

            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callSingleApi.value = false
                singleApiResponseFailure.value = t
            }

            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }

    fun deleteOrder(){
        callDeleteApi.value = true
        object : RetrofitApiManager<RequisitionResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).removeRequest(DashboardFragment.requisitionId))
            }
            override fun onSuccess(t: RequisitionResponseModel?) {
                callDeleteApi.value = false
                //saving user details
                apiRequisitionSuccess.value = t!!
            }

            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callDeleteApi.value = false
                apiRequisitionFailure.value = t
            }

            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }






}