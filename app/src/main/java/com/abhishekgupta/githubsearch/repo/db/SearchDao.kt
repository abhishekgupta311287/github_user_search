package com.abhishekgupta.githubsearch.repo.db

import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User

interface SearchDao {

    suspend fun getUser(user: String): User?

    suspend fun getUserFollowers(user: String, limit: Int, start: Int): List<Follower>

    suspend fun getUserFollowing(user: String, limit: Int, start: Int): List<Following>

    suspend fun insertUser(user: User)

    suspend  fun insertUserFollowers(userName: String, followers: List<Follower>)

    suspend fun insertUserFollowing(userName: String, following: List<Following>)

}