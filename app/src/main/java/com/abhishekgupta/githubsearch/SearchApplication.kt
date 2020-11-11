package com.abhishekgupta.githubsearch

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.abhishekgupta.githubsearch.di.appModule
import com.abhishekgupta.githubsearch.di.dbModule
import com.abhishekgupta.githubsearch.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SearchApplication)
            modules(
                listOf(
                    dbModule,
                    networkModule,
                    appModule
                )
            )
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

    }
}