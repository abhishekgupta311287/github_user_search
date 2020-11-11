package com.abhishekgupta.githubsearch.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.Resource
import com.abhishekgupta.githubsearch.model.UserDetail
import com.abhishekgupta.githubsearch.repo.SearchRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SearchViewModel(
    private val repo: SearchRepository
) : ViewModel() {

    val userLiveData: MutableLiveData<Resource<UserDetail>> = MutableLiveData()
    val followerLiveData: MutableLiveData<Resource<List<Follower>>> = MutableLiveData()
    val followingLiveData: MutableLiveData<Resource<List<Following>>> = MutableLiveData()

    fun getUserDetails(userName: String) {
        viewModelScope.launch {

            userLiveData.value = Resource.Loading()

            val user = async { repo.getUser(userName) }.await()

            val followers = async { repo.getUserFollowers(userName) }.await()

            val following = async { repo.getUserFollowing(userName) }.await()

            if (user != null) {
                userLiveData.value = Resource.Success(
                    UserDetail(user, followers, following)
                )
            } else {
                userLiveData.value = Resource.Error("Unable to fetch user details")
            }

        }
    }


    suspend fun getUserFollowers(userName: String) {
        followerLiveData.value = Resource.Loading()
        val followers = repo.getUserFollowers(userName)

        if (followers.isEmpty()) {
            followerLiveData.value = Resource.Error("No more followers")
        } else {
            followerLiveData.value = Resource.Success(followers)
        }
    }

    suspend fun getUserFollowing(userName: String) {
        followingLiveData.value = Resource.Loading()

        val following = repo.getUserFollowing(userName)

        if (following.isEmpty()) {
            followingLiveData.value = Resource.Error("No more following")
        } else {
            followingLiveData.value = Resource.Success(following)
        }
    }

}
