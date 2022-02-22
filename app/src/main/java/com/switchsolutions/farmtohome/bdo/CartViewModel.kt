package com.switchsolutions.farmtohome.bdo

import androidx.lifecycle.*
import com.switchsolutions.farmtohome.bdo.room_db.CartEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.CartRepository
import com.switchsolutions.farmtohome.bdo.room_db.ProductDatabase
import com.switchsolutions.farmtohome.bdo.room_db.ProductRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: CartEntityClass
    val inputName = MutableLiveData<String>()
    val inputQuantity = MutableLiveData<String>()
    var cartStatus: MutableLiveData<Boolean> = MutableLiveData()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        inputName.value = ""
        inputQuantity.value = ""
        cartStatus.value = false
    }

    fun initUpdateAndDelete(product: CartEntityClass) {
        inputName.value = product.productName
        inputQuantity.value = product.quantity
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = product
    }

    fun saveOrUpdate(productName: String,productId: Int, customerId: Int,  quantity: String, unit: String,  customerName: String, deliveryDate: String, previousCustomerId: Int ) {
        if (productName.isEmpty()) {
            statusMessage.value = Event("Please enter product's name")
        } else if (quantity.isEmpty()) {
            statusMessage.value = Event("Please enter product's email")
        } else if (customerId == previousCustomerId){
            cartStatus.value = false
                insertProduct(CartEntityClass(0,customerId, productName, quantity, unit, customerName, deliveryDate,productId))
        }
        else {
            cartStatus.value = true
            clearAll()
            insertProduct(CartEntityClass(0,customerId, productName, quantity,unit, customerName, deliveryDate, productId))
        }
    }

    private fun insertProduct(product: CartEntityClass) = viewModelScope.launch {
        val newRowId = repository.insert(product)
        if (newRowId > -1) {
            statusMessage.value = Event("Product Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }


    private fun updateCartProduct(product: CartEntityClass) = viewModelScope.launch {
        val noOfRows = repository.update(product)
        if (noOfRows > 0) {
            inputName.value = ""
            inputQuantity.value = ""
            isUpdateOrDelete = false
            statusMessage.value = Event("$noOfRows Row Updated Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun getSavedProducts() = liveData {
        repository.product.collect {
            emit(it)
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            deleteProduct(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

     fun deleteProduct(product: CartEntityClass) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(product)
        if (noOfRowsDeleted > 0) {
            statusMessage.value = Event("$noOfRowsDeleted Row Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

     fun clearAll() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
           // statusMessage.value = Event("$noOfRowsDeleted products Deleted Successfully")
        } else {
            cartStatus.value = false
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun getCartItemCount() : Int {
        return repository.getItemCount()


    }
}