package com.switchsolutions.farmtohome.bdo.room_db.product



class ProductRepository(private val daoProduct: ProductDAO) {

    val product = daoProduct.getAllProducts()

    suspend fun insert(productEntityClass: ProductEntityClass): Long {
        return daoProduct.insertProduct(productEntityClass)
    }

    suspend fun deleteAll(): Int {
        return daoProduct.deleteAll()
    }
}