package com.abhishekgupta.githubsearch.view.adapter

import android.view.View
import com.abhishekgupta.githubsearch.R
import com.abhishekgupta.githubsearch.model.Follower
import com.abhishekgupta.githubsearch.model.Following
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_follow_item.view.*

class FollowViewHolder<T>(view: View) : BaseViewHolder<T>(view) {
    override fun bind(t: T?) {
        when (t) {
            is Follower -> {
                bind(t.avatar, t.login)

            }
            is Following -> {
                bind(t.avatar, t.login)
            }
        }
    }

    private fun bind(avatar: String, login: String) {
        Glide
            .with(itemView.followAvatar)
            .load(avatar)
            .placeholder(R.drawable.placeholder_circle)
            .circleCrop()
            .into(itemView.followAvatar)

        itemView.login.text = login
    }

}

