package com.abhishekgupta.githubsearch.repo

import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User
import com.abhishekgupta.githubsearch.repo.db.SearchDao
import com.abhishekgupta.githubsearch.repo.network.GithubApi

class SearchRepositoryImpl(
    private val api: GithubApi,
    private val dao: SearchDao
) : SearchRepository {

    var followerPage = 1
    var followingPage = 1

    override suspend fun getUser(userName: String): User? {
        return try {
            var user = dao.getUser(userName)

            if (user == null) {
                user = api.getUser(userName)
                user?.let {
                    dao.insertUser(it)
                }
            }

            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUserFollowers(userName: String): List<Follower> {
        val start = LIMIT * (followerPage - 1)

        var followers = dao.getUserFollowers(userName, LIMIT, start)

        if (followers.isEmpty()) {
            followers = api.getUserFollowers(userName, LIMIT, followerPage)

            if (followers.isNotEmpty()) {
                dao.insertUserFollowers(userName, followers)
            }

        }
        followerPage++

        return followers
    }

    override suspend fun getUserFollowing(userName: String): List<Following> {
        val start = LIMIT * (followingPage - 1)

        var following = dao.getUserFollowing(userName, LIMIT, start)

        if (following.isEmpty()) {
            following = api.getUserFollowing(userName, LIMIT, followingPage)
            if (following.isNotEmpty()) {
                dao.insertUserFollowing(userName, following)
            }

        }
        followingPage++

        return following
    }

    companion object {
        const val LIMIT = 10
    }
}