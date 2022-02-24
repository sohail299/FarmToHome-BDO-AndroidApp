package com.switchsolutions.farmtohome.bdo.room_db.branch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branch_data_table")
data class BranchEntityClass(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "value")
    var value: Int,

    @ColumnInfo(name = "label")
    var label: String,

    @ColumnInfo(name = "code")
    var code: String

)