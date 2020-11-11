package com.abhishekgupta.githubsearch.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "following", primaryKeys = ["id", "user"])
data class Following(
    val user:String, // from user table
    val id: Int,
    val login: String,
    @SerializedName("avatar_url") val avatar: String
)