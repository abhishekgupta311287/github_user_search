package com.abhishekgupta.githubsearch.repo

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User
import com.abhishekgupta.githubsearch.repo.db.SearchDao
import com.abhishekgupta.githubsearch.repo.network.GithubApi
import com.abhishekgupta.githubsearch.testFollowerList
import com.abhishekgupta.githubsearch.testFollowingList
import com.abhishekgupta.githubsearch.testUser
import com.abhishekgupta.githubsearch.userName
import com.abhishekgupta.githubsearch.util.getCurrentTimeMillis
import com.abhishekgupta.githubsearch.util.isNetworkAvailable
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

@ExperimentalCoroutinesApi
class SearchRepositoryImplTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val api = mockk<GithubApi>(relaxed = true)
    private val dao = mockk<SearchDao>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    @Test
    fun `test no user in db`() = runBlockingTest {
        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUser(userName) }.returns(null)
        coEvery { context.isNetworkAvailable() }.returns(true)
        coEvery { api.getUser(userName) }.returns(testUser)

        val user = repo.getUser(userName)

        coVerify { api.getUser(userName) }
        coVerify { dao.insertUser(any()) }
        assertNotNull(user)
        assertEquals(userName, user?.login)

    }

    @Test
    fun `test user returned from db`() = runBlockingTest {
        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUser(userName) }.returns(testUser.copy(lastRefresh = getCurrentTimeMillis()))
        coEvery { context.isNetworkAvailable() }.returns(true)

        val user = repo.getUser(userName)

        coVerify(exactly = 0) { api.getUser(userName) }
        coVerify (exactly = 0){ dao.insertUser(testUser) }

        assertNotNull(user)
        assertEquals(userName, user?.login)

    }

    @Test
    fun `test user cache in db expired`() = runBlockingTest {
        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUser(userName) }.returns(testUser)
        coEvery { context.isNetworkAvailable() }.returns(true)
        coEvery { api.getUser(userName) }.returns(testUser)

        val user = repo.getUser(userName)

        coVerify(exactly = 1) { api.getUser(userName) }
        assertNotNull(user)
        assertEquals(userName, user?.login)

    }

    @Test
    fun `test user when network exception and user available in cache`() = runBlockingTest {
        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUser(userName) }.returns(testUser)
        coEvery { context.isNetworkAvailable() }.returns(true)
        coEvery { api.getUser(userName) }.throws(Exception("403"))

        val user = repo.getUser(userName)

        coVerify(exactly = 1) { api.getUser(userName) }
        assertNotNull(user)
        assertEquals(userName, user?.login)
    }

    @Test
    fun `when user followers list is not cached`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUserFollowers(userName, 10, 0) }.returns(emptyList())
        coEvery { context.isNetworkAvailable() }.returns(true)
        coEvery { api.getUserFollowers(userName, 10, 1)}.returns(testFollowerList)

        val followers = repo.getUserFollowers(userName)

        coVerify { api.getUserFollowers(userName, 10, 1) }
        coVerify { dao.insertUserFollowers(userName, followers) }
        assertEquals(2, followers.size)
    }

    @Test
    fun `when user followers list is cached`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUser(userName) }.returns(testUser.copy(lastRefresh = getCurrentTimeMillis()))
        coEvery { dao.getUserFollowers(userName, 10, 0) }.returns(testFollowerList)
        coEvery { context.isNetworkAvailable() }.returns(true)

        repo.getUser(userName)
        val followers = repo.getUserFollowers(userName)

        coVerify(exactly = 0) { api.getUserFollowers(userName, 10, 1) }
        coVerify(exactly = 0) { dao.insertUserFollowers(userName, followers) }
        assertEquals(2, followers.size)
    }

    @Test
    fun `when user followers list is cache expired`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUserFollowers(userName, 10, 0) }.returns(testFollowerList)
        coEvery {  api.getUserFollowers(userName, 10, 1) }.returns(testFollowerList)
        coEvery { context.isNetworkAvailable() }.returns(true)

        val followers = repo.getUserFollowers(userName)

        coVerify { api.getUserFollowers(userName, 10, 1) }
        coVerify { dao.insertUserFollowers(userName, followers) }
        assertEquals(2, followers.size)
    }

    @Test
    fun `followers list in case of exception with cached data`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUserFollowers(userName, 10, 0) }.returns(testFollowerList)
        coEvery {  api.getUserFollowers(userName, 10, 1) }.throws(Exception("403"))
        coEvery { context.isNetworkAvailable() }.returns(true)

        val followers = repo.getUserFollowers(userName)

        coVerify { api.getUserFollowers(userName, 10, 1) }
        assertEquals(2, followers.size)
    }




    @Test
    fun `when user following list is not cached`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUserFollowing(userName, 10, 0) }.returns(emptyList())
        coEvery { context.isNetworkAvailable() }.returns(true)
        coEvery { api.getUserFollowing(userName, 10, 1)}.returns(testFollowingList)

        val following = repo.getUserFollowing(userName)

        coVerify { api.getUserFollowing(userName, 10, 1) }
        coVerify { dao.insertUserFollowing(userName, following) }
        assertEquals(2, following.size)
    }

    @Test
    fun `when user following list is cached`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUser(userName) }.returns(testUser.copy(lastRefresh = getCurrentTimeMillis()))
        coEvery { dao.getUserFollowing(userName, 10, 0) }.returns(testFollowingList)
        coEvery { context.isNetworkAvailable() }.returns(true)

        repo.getUser(userName)
        val following = repo.getUserFollowing(userName)

        coVerify(exactly = 0) { api.getUserFollowing(userName, 10, 1) }
        coVerify(exactly = 0) { dao.insertUserFollowing(userName, following) }
        assertEquals(2, following.size)
    }

    @Test
    fun `when user following list is cache expired`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUserFollowing(userName, 10, 0) }.returns(testFollowingList)
        coEvery {  api.getUserFollowing(userName, 10, 1) }.returns(testFollowingList)
        coEvery { context.isNetworkAvailable() }.returns(true)

        val following = repo.getUserFollowing(userName)

        coVerify { api.getUserFollowing(userName, 10, 1) }
        coVerify { dao.insertUserFollowing(userName, following) }
        assertEquals(2, following.size)
    }

    @Test
    fun `following list in case of exception with cached data`() = runBlockingTest {

        val repo = SearchRepositoryImpl(api, dao, context)

        coEvery { dao.getUserFollowing(userName, 10, 0) }.returns(testFollowingList)
        coEvery {  api.getUserFollowing(userName, 10, 1) }.throws(Exception("403"))
        coEvery { context.isNetworkAvailable() }.returns(true)

        val following = repo.getUserFollowing(userName)

        coVerify { api.getUserFollowing(userName, 10, 1) }
        assertEquals(2, following.size)
    }
}