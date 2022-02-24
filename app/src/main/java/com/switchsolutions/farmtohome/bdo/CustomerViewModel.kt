package com.switchsolutions.farmtohome.bdo

import androidx.lifecycle.*
import com.switchsolutions.farmtohome.bdo.room_db.customer.CustomerEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.customer.CustomerRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage
     fun insertProduct(product: CustomerEntityClass) = viewModelScope.launch {
        val newRowId = repository.insert(product)
        if (newRowId > -1) {
            statusMessage.value = Event("SUCCESS")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }


    fun getSavedProducts() = liveData {
        repository.product.collect {
            emit(it)
        }
    }

    fun clearAll() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
            statusMessage.value = Event("SUCCESS")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }
}