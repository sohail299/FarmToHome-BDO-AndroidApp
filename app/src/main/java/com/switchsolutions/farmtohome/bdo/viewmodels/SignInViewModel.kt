package com.switchsolutions.farmtohome.bdo.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.switchsolutions.farmtohome.bdo.*
import com.switchsolutions.farmtohome.bdo.api.RestApiClient
import com.switchsolutions.farmtohome.bdo.callbacks.ErrorDto
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import com.switchsolutions.farmtohome.bdo.responsemodels.GenericResponseModel
import timber.log.BuildConfig
import timber.log.Timber

class SignInViewModel : ViewModel() {

    private val loginRequestModel: MutableLiveData<LoginRequestModel> = MutableLiveData()
    var signInEmailErrorStatus: MutableLiveData<Boolean> = MutableLiveData()
    var signInPasswordErrorStatus: MutableLiveData<Boolean> = MutableLiveData()
    var clearAllInputErrors: MutableLiveData<Boolean> = MutableLiveData()
    var callSignInApi: MutableLiveData<Boolean> = MutableLiveData()
    var apiResponseSuccess: MutableLiveData<LoginResponseModel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()

    init {
        loginRequestModel.value = if (BuildConfig.DEBUG) LoginRequestModel("", "")
        else LoginRequestModel("", "")
        signInEmailErrorStatus.value = false
        signInPasswordErrorStatus.value = false
        clearAllInputErrors.value = true
        callSignInApi.value = false
    }

    fun getLoginRequestModel(): MutableLiveData<LoginRequestModel> {
        return loginRequestModel
    }

    fun signInClicked() {
        clearAllInputErrors.value = false
        //Validating data
        loginRequestModel.value?.msisdn = loginRequestModel.value?.msisdn?.trim() ?: ""
        if (!ValidationUtil.isPhoneNumberValid(loginRequestModel.value?.msisdn!!) && !ValidationUtil.isANumber(
                loginRequestModel.value?.msisdn!!
            )
        ) {
            signInEmailErrorStatus.value = true
            return
        }
        if (!ValidationUtil.isPasswordValid(loginRequestModel.value?.password!!)) {
            signInPasswordErrorStatus.value = true
            return
        }
        callSignInApi.value = true
        //call api here
        object : RetrofitApiManager<LoginResponseModel>(AppController.ApplicationContext) {

            init {
                callServer(RestApiClient.getClient(addHeaders = false).signInCall(loginRequestModel.value?.msisdn ?: "", loginRequestModel.value?.password ?: "", "3"))
            }

            override fun onSuccess(t: LoginResponseModel?) {
                callSignInApi.value = false
                //saving user details
                val sharedPref = SharedPrefUtils.getInstance(AppController.ApplicationContext)
                sharedPref.setValue(SharedPrefUtils.USER_PROFILE, Gson().toJson(t?.data?.name))
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
}