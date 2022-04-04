package com.switchsolutions.farmtohome.bdo.fragments


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.responsemodels.AddCustomerResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.sectorsresponse.SectorsResponseModel
import timber.log.BuildConfig
import timber.log.Timber

class AddCustomerViewModel : ViewModel() {

    private val addCustomerRequestModel: MutableLiveData<AddCustomerRequestModel> = MutableLiveData()
    var emailErrorStatus: MutableLiveData<Boolean> = MutableLiveData()
    var passwordErrorStatus: MutableLiveData<Boolean> = MutableLiveData()
    var clearAllInputErrors: MutableLiveData<Boolean> = MutableLiveData()
    var callSignInApi: MutableLiveData<Boolean> = MutableLiveData()
    var apiResponseSuccess: MutableLiveData<LoginResponseModel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    private val apiSectorsSuccess = MutableLiveData<Event<SectorsResponseModel>>()
    val statusSectorsSuccess: MutableLiveData<Event<SectorsResponseModel>>
        get() = apiSectorsSuccess

    private val apiAddUserCall = MutableLiveData<Event<Boolean>>()
    val statusAddUserCall: MutableLiveData<Event<Boolean>>
        get() = apiAddUserCall

    private val apiAddCustomerSuccess = MutableLiveData<Event<AddCustomerResponseModel>>()
    val statusAddCustomerSuccess: MutableLiveData<Event<AddCustomerResponseModel>>
        get() = apiAddCustomerSuccess

    private val apiSectorsFailure = MutableLiveData<Event<ErrorDto>>()
    val statusSectorsFailure: MutableLiveData<Event<ErrorDto>>
        get() = apiSectorsFailure

    private val apiAddCustomerFailure = MutableLiveData<Event<ErrorDto>>()
    val statusAddCustomerFailure: MutableLiveData<Event<ErrorDto>>
        get() = apiAddCustomerFailure

    init {
        addCustomerRequestModel.value = if (BuildConfig.DEBUG) AddCustomerRequestModel("", "","","","","",
            ArrayList()
        )
        else AddCustomerRequestModel("", "","","","","", ArrayList())
        emailErrorStatus.value = false
        passwordErrorStatus.value = false
        clearAllInputErrors.value = true
        callSignInApi.value = false
    }
    fun getAddCustomerRequestModel(): MutableLiveData<AddCustomerRequestModel> {
        return addCustomerRequestModel
    }

    fun addCustomerBtnClicked(addCustomerObj: JsonObject) {
        apiAddUserCall.value = Event(true)
        //call api here
        object : RetrofitApiManager<AddCustomerResponseModel>(AppController.ApplicationContext) {

            init {
                callServer(RestApiClient.getClient(addHeaders = true).addCustomer(addCustomerObj))
            }
            override fun onSuccess(t: AddCustomerResponseModel?) {
                apiAddUserCall.value = Event(false)
                //saving user details
                apiAddCustomerSuccess.value = Event(t!!)
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                apiAddUserCall.value = Event(false)
                apiAddCustomerFailure.value = Event(t)
            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
    fun getSectors(cityId: Int){


        object : RetrofitApiManager<SectorsResponseModel>(AppController.ApplicationContext) {

            init {
                callServer(RestApiClient.getClient(addHeaders = true).getSectors(cityId))
            }
            override fun onSuccess(t: SectorsResponseModel?) {
                //saving user details
                apiSectorsSuccess.value = Event(t!!)
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                apiSectorsFailure.value = Event(t)
            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
}