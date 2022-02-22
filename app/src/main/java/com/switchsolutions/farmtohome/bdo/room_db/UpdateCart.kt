package com.switchsolutions.farmtohome.bdo.room_db

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "cart_data_table")
class UpdateCart {
    @ColumnInfo(name = "id")
    var id: Long = 0

    @ColumnInfo(name = "product_quantity")
    private val product_quantity: String? = null
}