package com.switchsolutions.farmtohome.bdo.room_db.customer


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CustomerEntityClass::class], version = 1)
abstract class CustomerDatabase : RoomDatabase() {
    abstract val customerDAO: CustomerDAO

    companion object {
        @Volatile
        private var INSTANCE: CustomerDatabase? = null
        fun getInstance(context: Context): CustomerDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CustomerDatabase::class.java,
                        "customer_data_database"
                    ).build()
                }
                return instance
            }
        }
    }
}


