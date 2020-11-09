package com.abhishekgupta.githubsearch.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val login: String,
    val name: String,
    val bio: String,
    val followers: Int,
    val following: Int,
    @SerializedName("avatar_url") val avatar: String,
    @SerializedName("followers_url") val followersUrl: String,
    @SerializedName("following_url") val followingURl: String
)