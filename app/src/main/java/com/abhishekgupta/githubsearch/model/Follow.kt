package com.abhishekgupta.githubsearch.model

import com.google.gson.annotations.SerializedName

data class Follow(
    val id: Int,
    val login: String,
    @SerializedName("avatar_url") val avatar: String
)