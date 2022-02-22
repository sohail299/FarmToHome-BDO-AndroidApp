package com.switchsolutions.farmtohome.bdo

import android.content.Context
import androidx.multidex.MultiDexApplication
import timber.log.Timber

class AppController : MultiDexApplication() {

    companion object {
        lateinit var ApplicationContext : Context
    }

    override fun onCreate() {
        super.onCreate()
        ApplicationContext = this
        Timber.plant(Timber.DebugTree())
        Timber.tag("BDO")
    }


}