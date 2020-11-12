package com.abhishekgupta.githubsearch.repo.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers
import org.junit.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStreamReader

class GithubApiTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var api: GithubApi
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(GithubApi::class.java)

    }

    @Test
    fun `user api success`() = runBlocking {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(readStringFromFile("user.json"))
            }
        }
        val user = api.getUser("abhishekgupta311287")
        val request = mockWebServer.takeRequest()

        Assert.assertThat(request.path, CoreMatchers.`is`("/users/abhishekgupta311287"))

        Assert.assertNotNull(user)

        Assert.assertThat(user?.id, CoreMatchers.`is`(9594724))
        Assert.assertThat(user?.login, CoreMatchers.`is`("abhishekgupta311287"))
        Assert.assertThat(user?.name, CoreMatchers.`is`("Abhishek Gupta"))
        Assert.assertThat(
            user?.avatar,
            CoreMatchers.`is`("https://avatars2.githubusercontent.com/u/9594724?v=4")
        )
        Assert.assertThat(
            user?.bio,
            CoreMatchers.`is`("Lead Engineer - Product Development Full Stack Dev | Redux | Kotlin | Android | Java | Payment Gateways ")
        )

        Assert.assertThat(user?.followersCount, CoreMatchers.`is`(1))
        Assert.assertThat(user?.followingCount, CoreMatchers.`is`(0))
        Assert.assertThat(user?.repos, CoreMatchers.`is`(6))

    }

    @Test
    fun `follower api success`() = runBlocking {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(readStringFromFile("followers.json"))
            }
        }
        val followers = api.getUserFollowers("abhishekgupta311287", 10, 1)
        val request = mockWebServer.takeRequest()

        Assert.assertThat(
            request.path,
            CoreMatchers.`is`("/users/abhishekgupta311287/followers?per_page=10&page=1")
        )

        Assert.assertEquals(1, followers.size)

        val follower = followers[0]

        Assert.assertThat(
            follower.avatar,
            CoreMatchers.`is`("https://avatars2.githubusercontent.com/u/50449783?v=4")
        )
        Assert.assertThat(follower.id, CoreMatchers.`is`(50449783))
        Assert.assertThat(follower.login, CoreMatchers.`is`("farrahtito789"))

    }

    @Test
    fun `following api success`() = runBlocking {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(readStringFromFile("followers.json"))
            }
        }
        val following = api.getUserFollowing("abhishekgupta311287", 10, 1)
        val request = mockWebServer.takeRequest()

        Assert.assertThat(
            request.path,
            CoreMatchers.`is`("/users/abhishekgupta311287/following?per_page=10&page=1")
        )

        Assert.assertEquals(1, following.size)

        val follower = following[0]

        Assert.assertThat(
            follower.avatar,
            CoreMatchers.`is`("https://avatars2.githubusercontent.com/u/50449783?v=4")
        )
        Assert.assertThat(follower.id, CoreMatchers.`is`(50449783))
        Assert.assertThat(follower.login, CoreMatchers.`is`("farrahtito789"))

    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    private fun readStringFromFile(fileName: String): String {
        try {
            val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
            val builder = StringBuilder()
            val reader = InputStreamReader(inputStream, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }
            return builder.toString()
        } catch (e: IOException) {
            throw e
        }
    }

}