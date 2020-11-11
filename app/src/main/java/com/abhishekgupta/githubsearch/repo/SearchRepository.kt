package com.abhishekgupta.githubsearch.repo

import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User

interface SearchRepository {

    suspend fun getUser(userName: String): User?

    suspend fun getUserFollowers(userName: String): List<Follower>

    suspend fun getUserFollowing(userName: String): List<Following>
}