package com.abhishekgupta.githubsearch.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishekgupta.githubsearch.R
import com.abhishekgupta.githubsearch.model.*
import com.abhishekgupta.githubsearch.util.isNetworkAvailable
import com.abhishekgupta.githubsearch.view.adapter.FollowAdapter
import com.abhishekgupta.githubsearch.viewmodel.SearchViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.layout_follower.view.*
import kotlinx.android.synthetic.main.layout_user.*
import kotlinx.android.synthetic.main.layout_user_follow_details.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.shimmer_effect_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val THRESHOLD = 2

        fun newInstance() = SearchFragment()
    }

    private val followerAdapter by lazy { FollowAdapter<Follower>() }
    private val followingAdapter by lazy { FollowAdapter<Following>() }

    private var isFetchingFollowers = false
    private var isFetchingFollowing = false
    private var followersCount = 0
    private var followingCount = 0

    private val viewModel by viewModel<SearchViewModel>()// by koin dependency injection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSearchView()
        setFollowersView()
        setFollowingView()
        observerUserDetails()
        observerFollowerData()
        observerFollowingData()
    }

    private fun setSearchView() {
        searchBtn.setOnClickListener {
            fetchUserDetails()
            searchBox.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }
        searchBox.setOnEditorActionListener { _, _, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                fetchUserDetails()
            }
            false
        }
    }

    private fun fetchUserDetails() {
        val userName = searchBox.text.toString()

        if (userName.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.enter_user_name),
                Toast.LENGTH_LONG
            )
                .show()
        } else {
            viewModel.getUserDetails(userName)
        }
    }

    private fun setFollowingView() {
        layout_following.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        layout_following.recyclerView.setHasFixedSize(true)
        layout_following.title.text = getString(R.string.following_title)
        layout_following.noData.text = getString(R.string.no_following)

        layout_following.recyclerView.adapter = followingAdapter

        layout_following.recyclerView.addItemDecoration(
            DividerItemDecoration(
                layout_follower.recyclerView.context,
                (layout_follower.recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )

        layout_following.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int, dy: Int
            ) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val itemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isFetchingFollowing
                    && itemCount <= lastVisibleItemPosition + THRESHOLD
                    && itemCount < followingCount
                    && requireContext().isNetworkAvailable() == true
                ) {
                    viewModel.getUserFollowing()
                    isFetchingFollowing = true
                }
            }
        })
    }

    private fun setFollowersView() {
        layout_follower.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        layout_follower.recyclerView.setHasFixedSize(true)
        layout_follower.title.text = getString(R.string.followers_title)
        layout_follower.noData.text = getString(R.string.no_followers)

        layout_follower.recyclerView.adapter = followerAdapter

        layout_follower.recyclerView.addItemDecoration(
            DividerItemDecoration(
                layout_follower.recyclerView.context,
                (layout_follower.recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )

        layout_follower.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int, dy: Int
            ) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val itemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isFetchingFollowers
                    && itemCount <= lastVisibleItemPosition + THRESHOLD
                    && itemCount < followersCount
                    && requireContext().isNetworkAvailable() == true
                ) {
                    viewModel.getUserFollowers()
                    isFetchingFollowers = true
                }
            }
        })
    }

    private fun observerUserDetails() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    shimmerLayout.hideShimmer()
                    shimmerLayout.visibility = View.GONE
                    errorLayout.visibility = View.GONE

                    it.data?.let { userDetail ->
                        setUserDetails(userDetail.user)
                        setFollowers(userDetail)
                        setFollowing(userDetail)
                        userLayout.visibility = View.VISIBLE
                    }

                }
                is Resource.Loading -> {
                    shimmerLayout.showShimmer(true)
                    shimmerLayout.visibility = View.VISIBLE
                    userLayout.visibility = View.GONE
                    errorLayout.visibility = View.GONE
                }
                is Resource.Error -> {
                    shimmerLayout.hideShimmer()
                    shimmerLayout.visibility = View.GONE
                    userLayout.visibility = View.GONE
                    errorLayout.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun observerFollowerData() {
        viewModel.followerLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    removeFollowerNullItem()
                    val previousSize = followerAdapter.list.size
                    followerAdapter.list.addAll(it.data as ArrayList<Follower?>)
                    followerAdapter.notifyItemRangeInserted(previousSize, it.data.size)
                }
                is Resource.Loading -> {
                    layout_follower.recyclerView.post {
                        followerAdapter.list.add(null)
                        followerAdapter.notifyItemInserted(followerAdapter.list.size - 1)
                    }
                }
                is Resource.Error -> {
                    removeFollowerNullItem()
                }
            }
        })
    }

    private fun removeFollowerNullItem() {
        isFetchingFollowers = false
        if (followerAdapter.list.remove(null)) {
            followerAdapter.notifyItemRemoved(followerAdapter.list.size)
        }
    }

    private fun observerFollowingData() {
        viewModel.followingLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    removeFollowingNullItem()
                    val previousSize = followingAdapter.list.size
                    followingAdapter.list.addAll(it.data as ArrayList<Following?>)
                    followingAdapter.notifyItemRangeInserted(previousSize, it.data.size)
                }
                is Resource.Loading -> {
                    layout_following.recyclerView.post {
                        followingAdapter.list.add(null)
                        followingAdapter.notifyItemInserted(followingAdapter.list.size - 1)
                    }

                }
                is Resource.Error -> {
                    removeFollowingNullItem()
                }
            }
        })
    }

    private fun removeFollowingNullItem() {
        isFetchingFollowing = false
        if (followingAdapter.list.remove(null)) {
            followingAdapter.notifyItemRemoved(followerAdapter.list.size)
        }
    }

    private fun setFollowing(userDetail: UserDetail) {
        if (userDetail.following.isNullOrEmpty()) {
            layout_following.noData.visibility = View.VISIBLE
            layout_following.recyclerView.visibility = View.INVISIBLE
        } else {
            layout_following.noData.visibility = View.INVISIBLE
            layout_following.recyclerView.visibility = View.VISIBLE
            followingAdapter.list = userDetail.following as ArrayList<Following?>
            followingAdapter.notifyDataSetChanged()
        }
    }

    private fun setFollowers(userDetail: UserDetail) {
        if (userDetail.followers.isNullOrEmpty()) {
            layout_follower.noData.visibility = View.VISIBLE
            layout_follower.recyclerView.visibility = View.INVISIBLE
        } else {
            layout_follower.noData.visibility = View.INVISIBLE
            layout_follower.recyclerView.visibility = View.VISIBLE
            followerAdapter.list = userDetail.followers as ArrayList<Follower?>
            followerAdapter.notifyDataSetChanged()
        }
    }

    private fun setUserDetails(user: User) {
        fullName.text = user.name
        userName.text = getString(R.string.login, user.login)
        bio.text = user.bio
        followersCount = user.followersCount
        followingCount = user.followingCount
        followers.text =
            getString(R.string.followers_count, user.followersCount)
        following.text =
            getString(R.string.following_count, user.followingCount)
        repos.text = getString(R.string.repos_count, user.repos)

        Glide
            .with(userAvatar)
            .load(user.avatar)
            .placeholder(R.drawable.placeholder_circle)
            .circleCrop()
            .into(userAvatar)
    }

}
