package com.switchsolutions.farmtohome.bdo

import androidx.lifecycle.*
import com.switchsolutions.farmtohome.bdo.room_db.BranchEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.BranchRespository
import com.switchsolutions.farmtohome.bdo.room_db.ProductEntityClass
import com.switchsolutions.farmtohome.bdo.room_db.ProductRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BranchViewModel(private val repository: BranchRespository) : ViewModel() {

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage
    fun insertBranch(branch: BranchEntityClass) = viewModelScope.launch {
        val newRowId = repository.insert(branch)
        if (newRowId > -1) {
            statusMessage.value = Event("Product Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }


    fun getSavedBranches() = liveData {
        repository.branch.collect {
            emit(it)
        }
    }

    fun clearAll() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
            statusMessage.value = Event("$noOfRowsDeleted products Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }
}