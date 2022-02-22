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

class CustomersApiViewModel : ViewModel() {

    var callCustomersInApi: MutableLiveData<Boolean> = MutableLiveData()

    var apiResponseSuccessCustomers: MutableLiveData<CustomersResponseModel> = MutableLiveData()
    var apiResponseFailureCustomers: MutableLiveData<ErrorDto> = MutableLiveData()


    init {
        callCustomersInApi.value = false
    }
    fun startObserver(cityId: Int) {
        callCustomersInApi.value = true
        //call api here
        object : RetrofitApiManager<CustomersResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).getCustomersDetail(cityId = cityId))
            }
            override fun onSuccess(t: CustomersResponseModel?) {
                callCustomersInApi.value = false
                //saving user details
                apiResponseSuccessCustomers.value = t!!
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callCustomersInApi.value = false
                apiResponseFailureCustomers.value = t

            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
}