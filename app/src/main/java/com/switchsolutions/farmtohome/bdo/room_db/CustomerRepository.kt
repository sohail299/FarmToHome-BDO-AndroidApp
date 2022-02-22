package com.switchsolutions.farmtohome.bdo.room_db

class CustomerRepository(private val dao: CustomerDAO) {

    val product = dao.getAllCustomers()

    suspend fun insert(customerEntity: CustomerEntityClass): Long {
        return dao.insertCustomer(customerEntity)
    }

    suspend fun delete(customerEntityClass: CustomerEntityClass): Int {
        return dao.deleteCustomer(customerEntityClass)
    }

    suspend fun deleteAll(): Int {
        return dao.deleteAll()
    }
}