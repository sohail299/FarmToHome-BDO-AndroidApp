package com.switchsolutions.farmtohome.bdo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.switchsolutions.farmtohome.bdo.room_db.BranchRespository
import com.switchsolutions.farmtohome.bdo.room_db.CustomerRepository
import com.switchsolutions.farmtohome.bdo.room_db.ProductRepository
import java.lang.IllegalArgumentException

class BranchViewModelFactory(
    private val repository: BranchRespository
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BranchViewModel::class.java)){
            return BranchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}