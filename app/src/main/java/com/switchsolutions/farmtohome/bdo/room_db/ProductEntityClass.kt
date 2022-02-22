package com.switchsolutions.farmtohome.bdo.room_db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_data_table")
data class ProductEntityClass(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "product_id")
    var product_id: Int,

    @ColumnInfo(name = "product_name")
    var productName: String,

    @ColumnInfo(name = "product_unit")
    var productUnit: String

)