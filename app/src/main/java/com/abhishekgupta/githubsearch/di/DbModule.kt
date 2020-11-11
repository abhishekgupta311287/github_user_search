package com.abhishekgupta.githubsearch.di

import androidx.room.Room
import com.abhishekgupta.githubsearch.repo.db.SearchDao
import com.abhishekgupta.githubsearch.repo.db.SearchDaoImpl
import com.abhishekgupta.githubsearch.repo.db.SearchDb
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(get(), SearchDb::class.java, "search.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single<SearchDao> { SearchDaoImpl((get() as SearchDb).searchDao()) }
}