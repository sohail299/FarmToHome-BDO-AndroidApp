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

class ProductsApiViewModel : ViewModel() {

    var callProductsInApi: MutableLiveData<Boolean> = MutableLiveData()

    var apiResponseSuccess: MutableLiveData<ProductsResponseModel> = MutableLiveData()
    var apiResponseFailure: MutableLiveData<ErrorDto> = MutableLiveData()


    init {
        callProductsInApi.value = false
    }
    fun startObserver(cityId: Int) {
        callProductsInApi.value = true
        //call api here
        object : RetrofitApiManager<ProductsResponseModel>(AppController.ApplicationContext) {
            init {
                callServer(RestApiClient.getClient(addHeaders = true).getProductsDetail(cityId = cityId))
            }
            override fun onSuccess(t: ProductsResponseModel?) {
                callProductsInApi.value = false
                //saving user details
                apiResponseSuccess.value = t!!
                if (t.data.isNotEmpty()) {
                    MainActivity.productsData = t.data
                }
            }
            override fun onFailure(t: ErrorDto) {
                Timber.e(t.message)
                callProductsInApi.value = false
                apiResponseFailure.value = t

            }
            override fun tokenRefreshed() {
                //nothing required in this case
            }
        }
    }
}