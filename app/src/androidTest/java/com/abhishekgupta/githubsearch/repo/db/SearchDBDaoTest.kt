package com.abhishekgupta.githubsearch.repo.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchDBDaoTest {

    private val userName = "abhishekgupta311287"

    private val testUser = User(
        1,
        userName,
        "Abhishek Gupta",
        "I am software engineer",
        0,
        10,
        10,
        10,
        "avatar_url"
    )

    private val testFollowerList = listOf(
        Follower(userName, 2, "ab", "url"),
        Follower("abhishekgupta311287", 3, "abs", "url")
    )

    private val testFollowingList = listOf(
        Following(userName, 4, "abi", "url"),
        Following(userName, 5, "rub", "url")
    )

    private lateinit var db: SearchDb
    private lateinit var dao: SearchDbDao

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, SearchDb::class.java)
            .fallbackToDestructiveMigration()
            .build()
        dao = db.searchDao()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() = runBlocking {
        db.close()
    }

    @Test
    fun insertUser() = runBlocking {
        dao.insertUser(testUser)

        val user = dao.getUser(userName)

        Assert.assertNotNull(user)
    }

    @Test
    fun insertFollowers() = runBlocking {
        dao.insertUserFollowers(userName, testFollowerList)

        var followers = dao.getUserFollowers(userName, 5, 0)

        Assert.assertEquals(2, followers.size)

        followers = dao.getUserFollowers(userName, 1, 1)

        Assert.assertEquals(1, followers.size)

    }

    @Test
    fun insertFollowing() = runBlocking {
        dao.insertUserFollowing(userName, testFollowingList)

        val following = dao.getUserFollowing(userName, 5, 1)

        Assert.assertEquals(1, following.size)

        Assert.assertEquals("rub", following[0].login)

    }

}