package com.switchsolutions.farmtohome.bdo.room_db.customer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_data_table")
data class CustomerEntityClass(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "customer_id")
    var customer_id: Int,

    @ColumnInfo(name = "customer_name")
    var customer_name: String

)