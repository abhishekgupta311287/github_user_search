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

    private var followerPage = 1
    private var followingPage = 1

    override suspend fun getUser(userName: String): User? {
        return try {
            // reset page count when user is searched
            followerPage = 1
            followingPage = 1
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
        return try {
            val start = LIMIT * (followerPage - 1)

            var followers = dao.getUserFollowers(userName, LIMIT, start)

            if (followers.isEmpty()) {
                followers = api.getUserFollowers(userName, LIMIT, followerPage)

                if (followers.isNotEmpty()) {
                    dao.insertUserFollowers(userName, followers)
                }

            }
            followerPage++

            followers
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserFollowing(userName: String): List<Following> {
        return try {
            val start = LIMIT * (followingPage - 1)

            var following = dao.getUserFollowing(userName, LIMIT, start)

            if (following.isEmpty()) {
                following = api.getUserFollowing(userName, LIMIT, followingPage)
                if (following.isNotEmpty()) {
                    dao.insertUserFollowing(userName, following)
                }

            }
            followingPage++

            following
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        const val LIMIT = 10
    }
}