package com.switchsolutions.farmtohome.bdo.api


import com.google.gson.JsonObject
import com.switchsolutions.farmtohome.bdo.requestmodels.CreateBdoRequestModel
import com.switchsolutions.farmtohome.bdo.responsemodels.*
import com.switchsolutions.farmtohome.bdo.responsemodels.pojo.LoginResponseModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiCommService {

    //Sign in api
    @POST("auth/login")
    fun signInCall(@Query("mobile") msisdn: String,
                   @Query("password") password: String,
                    @Query("platform") platform: String): Call<LoginResponseModel>

    @POST("bdorequest")
    fun createBdoRequest(@Body requestModel: JsonObject): Call<BDORequestResponsemodel>

    @POST("bdorequestsprocessing")
    fun getBdoRequests(@Query ("city_id")cityId: Int,
                       @Query("created_by") createdBy: Int): Call<DashboardResponseData>

    @GET("bdorequest")
    fun getOrdersToEdit(@Query ("request_id")reqId: Int): Call<GetOrdersForEditResponseModel>

    @GET("bdorequest")
    fun getSingleOrder(@Query ("request_id")reqId: Int): Call<GetOrdersForEditResponseModel>

    @GET("auth/refreshtoken")
    fun refreshToken(@Query("email") email : String) : Call<GenericResponseModel<LoginResponseModel>>

     @GET("names/customerbtb")
    fun getCustomersDetail(@Query("city_id") cityId : Int) : Call<CustomersResponseModel>

     @GET("names/products/cityproductswithunits")
        fun getProductsDetail(@Query("cityId") cityId : Int) : Call<ProductsResponseModel>

        @GET("bdorequestsremove")
        fun removeRequest(@Query("requisition_id") requisition_id : Int) : Call<RequisitionResponseModel>

        @POST("posbdorequestsstatus")
        fun getDispatchedOrders(@Body request : JsonObject) : Call<DispatchedOrdersResponseModel>

        @POST("bdostatusupdate")
        fun markAsDelivered(@Body request : JsonObject) : Call<DeliveryStatusResponseModel>

        @POST("bdorequestdevliered")
        fun getDeliveredOrders(@Body request : JsonObject) : Call<DeliveredResponseModel>

        @POST("bdoupdate")
        fun editOrder(@Body request : JsonObject) : Call<EditResponseModel>

}