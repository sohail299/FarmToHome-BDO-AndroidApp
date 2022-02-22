package com.switchsolutions.farmtohome.bdo.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.adapters.DashboardAdapter
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.fragments.DashboardFragment
import com.switchsolutions.farmtohome.bdo.fragments.DispatchFragment
import com.switchsolutions.farmtohome.bdo.responsemodels.*
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import timber.log.BuildConfig
import timber.log.Timber

class GetOrdersToEditViewModel : ViewModel() {

    var callSignInApi: MutableLiveData<Boolean> = MutableLiveData()

    var apiResponseSuccess: MutableLiveData<GetOrdersForEditResponseModel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    init {
        callSignInApi.value = false
    }
    fun startObserver() {
        callSignInApi.value = true
        //call api here
        object : RetrofitApiManager<GetOrdersForEditResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).getOrdersToEdit(DashboardFragment.requisitionIdEdit))
            }
            override fun onSuccess(t: GetOrdersForEditResponseModel?) {
                callSignInApi.value = false
                //saving user details
                apiResponseSuccess.value = t!!
                DashboardAdapter.dataEdit = t.products

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
}