package com.abhishekgupta.githubsearch

import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.abhishekgupta.githubsearch.model.User

const val userName = "abhishekgupta311287"

val testUser = User(
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

val testFollowerList = listOf(
    Follower(userName, 2, "ab", "url"),
    Follower("abhishekgupta311287", 3, "abs", "url")
)

val testFollowingList = listOf(
    Following(userName, 4, "abi", "url"),
    Following(userName, 5, "rub", "url")
)