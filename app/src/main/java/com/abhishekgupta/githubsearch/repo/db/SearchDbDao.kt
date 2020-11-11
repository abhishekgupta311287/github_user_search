package com.abhishekgupta.githubsearch.repo.db

import androidx.room.*
import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User

@Dao
interface SearchDbDao {

    @Query("select * from user where login = :user")
    suspend fun getUser(user: String): User?

    @Query("select * from followers where user = :user limit :limit offset :start")
    suspend fun getUserFollowers(user: String, limit: Int, start: Int): List<Follower>

    @Query("select * from following where user = :user limit :limit offset :start")
    suspend fun getUserFollowing(user: String, limit: Int, start: Int): List<Following>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowers(follower: Follower)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowing(following: Following)

    @Transaction
    suspend fun insertUserFollowers(userName: String, followers: List<Follower>) {
        for (f in followers)
            insertFollowers(f.copy(user = userName))
    }

    @Transaction
    suspend fun insertUserFollowing(userName: String, following: List<Following>) {
        for (f in following)
            insertFollowing(f.copy(user = userName))
    }

}