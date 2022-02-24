package com.switchsolutions.farmtohome.bdo.room_db.cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_data_table")
data class CartEntityClass(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "customer_id")
    var customer_id: Int,

    @ColumnInfo(name = "product_name")
    var productName: String,

    @ColumnInfo(name = "product_quantity")
    var quantity: String,

    @ColumnInfo(name = "product_unit")
    var productUnit: String,

    @ColumnInfo(name = "customer_name")
    var customerName: String,

    @ColumnInfo(name = "delivery_date")
    var deliveryDate: String,

    @ColumnInfo(name = "product_id")
    var productId: Int

)