package com.switchsolutions.farmtohome.bdo.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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



    var apiResponseSuccess: MutableLiveData<GetOrdersForEditResponseModel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()
    lateinit var editOrdersResponseData: GetOrdersForEditResponseModel

    private val statusSuccess = MutableLiveData<Event<GetOrdersForEditResponseModel>>()
    val status: MutableLiveData<Event<GetOrdersForEditResponseModel>>
        get() = statusSuccess

    val callEditApi: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val ApiStatus: MutableLiveData<Event<Boolean>>
        get() = callEditApi


    fun startObserver() {
        callEditApi.value = Event(true)
        //call api here
        object : RetrofitApiManager<GetOrdersForEditResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).getOrdersToEdit(DashboardFragment.requisitionIdEdit))
            }
            override fun onSuccess(t: GetOrdersForEditResponseModel?) {
                DashboardFragment.editOrders = t!!.products
                apiResponseSuccess.value = t!!
                callEditApi.value = Event(false)
                //saving user details
                statusSuccess.value = Event(t)
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callEditApi.value = Event(false)
                apiResponseFailure.value = t
            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
}