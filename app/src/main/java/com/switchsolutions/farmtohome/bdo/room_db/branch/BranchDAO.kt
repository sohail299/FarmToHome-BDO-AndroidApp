package com.switchsolutions.farmtohome.bdo.room_db.branch

import androidx.room.*
import com.switchsolutions.farmtohome.bdo.room_db.branch.BranchEntityClass
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDAO {

    @Insert
    suspend fun insertBranches(branchEntityClass: BranchEntityClass) : Long

    @Update
    suspend fun updateBranch(branchEntityClass: BranchEntityClass) : Int

    @Delete
    suspend fun deleteBranches(branchEntityClass: BranchEntityClass) : Int

    @Query("DELETE FROM branch_data_table")
    suspend fun deleteAll() : Int

    @Query("SELECT * FROM branch_data_table")
    fun getAllBranches():Flow<List<BranchEntityClass>>
}