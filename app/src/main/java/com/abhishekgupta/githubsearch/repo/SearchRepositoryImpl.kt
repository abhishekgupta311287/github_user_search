package com.abhishekgupta.githubsearch.repo

import android.content.Context
import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User
import com.abhishekgupta.githubsearch.repo.db.SearchDao
import com.abhishekgupta.githubsearch.repo.network.GithubApi
import com.abhishekgupta.githubsearch.util.getCurrentTimeMillis
import com.abhishekgupta.githubsearch.util.isCacheExpired
import com.abhishekgupta.githubsearch.util.isNetworkAvailable

class SearchRepositoryImpl(
    private val api: GithubApi,
    private val dao: SearchDao,
    private val context: Context
) : SearchRepository {

    private var followerPage = 1
    private var followingPage = 1
    private var isCacheExpired = true

    override suspend fun getUser(userName: String): User? {
        var user: User? = null
        return try {
            // reset page count when user is searched
            followerPage = 1
            followingPage = 1
            user = dao.getUser(userName)

            isCacheExpired = shouldFetchFromRemote(user)

            if (isCacheExpired && context.isNetworkAvailable() == true) {
                user = api.getUser(userName)
                user?.let {
                    dao.insertUser(it.copy(lastRefresh = getCurrentTimeMillis()))
                }
            }

            user
        } catch (e: Exception) {
            user
        }
    }

    override suspend fun getUserFollowers(userName: String): List<Follower> {
        var followers = emptyList<Follower>()
        return try {
            val start = LIMIT * (followerPage - 1)

            followers = dao.getUserFollowers(userName, LIMIT, start)

            if (context.isNetworkAvailable() == true && (followers.isEmpty() || isCacheExpired)) {
                followers = api.getUserFollowers(userName, LIMIT, followerPage)

                if (followers.isNotEmpty()) {
                    dao.insertUserFollowers(userName, followers)
                }

            }
            followerPage++

            followers
        } catch (e: Exception) {
            followers
        }
    }

    override suspend fun getUserFollowing(userName: String): List<Following> {
        var following = emptyList<Following>()
        return try {
            val start = LIMIT * (followingPage - 1)

            following = dao.getUserFollowing(userName, LIMIT, start)

            if (context.isNetworkAvailable() == true && (following.isEmpty() || isCacheExpired)) {
                following = api.getUserFollowing(userName, LIMIT, followingPage)
                if (following.isNotEmpty()) {
                    dao.insertUserFollowing(userName, following)
                }

            }
            followingPage++

            following
        } catch (e: Exception) {
            following
        }
    }

    private fun shouldFetchFromRemote(user: User?) =
        user == null || isCacheExpired(user.lastRefresh)

    companion object {
        const val LIMIT = 10
        const val CACHE_EXPIRY_HOURS = 2 // cache expiry time in hours
    }
}