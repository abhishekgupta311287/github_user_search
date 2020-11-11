package com.abhishekgupta.githubsearch.repo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User

@Database(
    entities = [User::class, Follower::class, Following::class],
    version = 1,
    exportSchema = false
)
abstract class SearchDb : RoomDatabase() {
    abstract fun searchDao(): SearchDbDao
}