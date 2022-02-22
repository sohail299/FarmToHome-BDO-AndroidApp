package com.switchsolutions.farmtohome.bdo.room_db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDAO {

    @Insert
    suspend fun insertCustomer(customerEntityClass: CustomerEntityClass): Long

    @Delete
    suspend fun deleteCustomer(customerEntityClass: CustomerEntityClass): Int

    @Query("DELETE FROM customer_data_table")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM customer_data_table")
    fun getAllCustomers(): Flow<List<CustomerEntityClass>>
}