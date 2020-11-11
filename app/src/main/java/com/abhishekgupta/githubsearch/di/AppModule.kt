package com.abhishekgupta.githubsearch.di

import com.abhishekgupta.githubsearch.repo.SearchRepository
import com.abhishekgupta.githubsearch.repo.SearchRepositoryImpl
import com.abhishekgupta.githubsearch.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
    viewModel { SearchViewModel(get()) }
}