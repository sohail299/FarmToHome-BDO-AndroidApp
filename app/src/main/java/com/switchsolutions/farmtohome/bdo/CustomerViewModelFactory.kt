package com.switchsolutions.farmtohome.bdo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.switchsolutions.farmtohome.bdo.room_db.CustomerRepository
import java.lang.IllegalArgumentException

class CustomerViewModelFactory(
    private val repository: CustomerRepository
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CustomerViewModel::class.java)){
            return CustomerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}