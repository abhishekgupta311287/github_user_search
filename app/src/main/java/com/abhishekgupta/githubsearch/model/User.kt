package com.abhishekgupta.githubsearch.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User(
    val id: Int,
    @PrimaryKey val login: String,
    val name: String,
    val bio: String?,
    val lastRefresh: Long,
    @SerializedName("public_repos") val repos: Int,
    @SerializedName("followers") val followersCount: Int,
    @SerializedName("following") val followingCount: Int,
    @SerializedName("avatar_url") val avatar: String
)