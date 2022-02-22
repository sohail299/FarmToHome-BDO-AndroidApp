package com.switchsolutions.farmtohome.bdo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.switchsolutions.farmtohome.bdo.CartViewModel
import com.switchsolutions.farmtohome.bdo.room_db.CartRepository
import java.lang.IllegalArgumentException

class CartViewModelFactory(
        private val repository: CartRepository
        ):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CartViewModel::class.java)){
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}