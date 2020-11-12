package com.abhishekgupta.githubsearch.view

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.abhishekgupta.githubsearch.R
import com.abhishekgupta.githubsearch.model.*
import com.abhishekgupta.githubsearch.viewmodel.SearchViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {
    @get:Rule
    val activityRule = ActivityTestRule(SearchActivity::class.java, false, false)

    private val userLiveData: MutableLiveData<Resource<UserDetail>> = MutableLiveData()
    private val followerLiveData: MutableLiveData<Resource<List<Follower>>> = MutableLiveData()
    private val followingLiveData: MutableLiveData<Resource<List<Following>>> = MutableLiveData()

    private val viewModel = mockk<SearchViewModel>(relaxed = true)
    private val myModule = module {
        viewModel { viewModel }
    }

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            modules(
                listOf(
                    myModule
                )
            )
        }
        every { viewModel.userLiveData }.returns(userLiveData)
        every { viewModel.followerLiveData }.returns(followerLiveData)
        every { viewModel.followingLiveData }.returns(followingLiveData)
    }

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

    private val testFollowerList = arrayListOf(
        Follower(userName, 2, "ab", "url"),
        Follower("abhishekgupta311287", 3, "abs", "url")
    )

    private val testFollowingList = arrayListOf(
        Following(userName, 4, "abi", "url"),
        Following(userName, 5, "rub", "url")
    )

    private val userDetail = UserDetail(
        testUser,
        testFollowerList,
        testFollowingList
    )

    @Test
    fun testSearchUI() {
        activityRule.launchActivity(Intent())

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.searchBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.searchBtn))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .perform(ViewActions.typeText("abhishekgupta311287"))

        Espresso.onView(ViewMatchers.withId(R.id.searchBtn))
            .perform(ViewActions.click())

        verify { viewModel.getUserDetails("abhishekgupta311287") }

    }

    @Test
    fun testUserDetailSuccessStateUI() {
        activityRule.launchActivity(Intent())

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .perform(ViewActions.typeText("abhishekgupta311287"))

        Espresso.onView(ViewMatchers.withId(R.id.searchBtn))
            .perform(ViewActions.click())

        userLiveData.postValue(Resource.Loading())

        Espresso.onView(ViewMatchers.withId(R.id.shimmerLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        userLiveData.postValue(Resource.Success(userDetail))

        Espresso.onView(ViewMatchers.withId(R.id.shimmerLayout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        Espresso.onView(ViewMatchers.withId(R.id.userName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("Abhishek Gupta"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("abhishekgupta311287"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.bio))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.followers))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.following))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.repos))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun testFollowerUI() {
        activityRule.launchActivity(Intent())

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .perform(ViewActions.typeText("abhishekgupta311287"))

        Espresso.onView(ViewMatchers.withId(R.id.searchBtn))
            .perform(ViewActions.click())

        userLiveData.postValue(Resource.Success(userDetail))

        Espresso.onView(ViewMatchers.withId(R.id.layout_follower))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("ab"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("abs"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun testFollowingUI() {
        activityRule.launchActivity(Intent())

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .perform(ViewActions.typeText("abhishekgupta311287"))

        Espresso.onView(ViewMatchers.withId(R.id.searchBtn))
            .perform(ViewActions.click())

        userLiveData.postValue(Resource.Success(userDetail))

        Espresso.onView(ViewMatchers.withId(R.id.layout_following))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("abi"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("rub"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun testNoFollowerAndFollowingUI() {
        activityRule.launchActivity(Intent())

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .perform(ViewActions.typeText("abhishekgupta311287"))

        Espresso.onView(ViewMatchers.withId(R.id.searchBtn))
            .perform(ViewActions.click())

        userLiveData.postValue(
            Resource.Success(
                UserDetail(
                    testUser,
                    arrayListOf(),
                    arrayListOf()
                )
            )
        )

        Espresso.onView(ViewMatchers.withText("No Followers."))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText("Not Following Anyone."))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun testErrorStateUI() {
        activityRule.launchActivity(Intent())

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.searchBox))
            .perform(ViewActions.typeText("abhishekgupta311287"))

        userLiveData.postValue(Resource.Error(""))

        Espresso.onView(ViewMatchers.withId(R.id.errorLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.userLayout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        Espresso.onView(ViewMatchers.withId(R.id.shimmerLayout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

    }

    @After
    fun teardown() {
        stopKoin()
    }
}