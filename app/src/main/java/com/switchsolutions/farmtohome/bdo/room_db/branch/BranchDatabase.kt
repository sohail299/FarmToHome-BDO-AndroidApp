package com.switchsolutions.farmtohome.bdo.room_db.branch

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BranchEntityClass::class], version = 1)
abstract class BranchDatabase : RoomDatabase() {
    abstract val branchDAO: BranchDAO

    companion object {
        @Volatile
        private var INSTANCE: BranchDatabase? = null
        fun getInstance(context: Context): BranchDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BranchDatabase::class.java,
                        "branch_data_database"
                    ).build()
                }
                return instance
            }
        }
    }
}

