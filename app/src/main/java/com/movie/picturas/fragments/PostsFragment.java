package com.movie.picturas.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.movie.picturas.R;
import com.movie.picturas.adapters.PostsAdapter;
import com.movie.picturas.models.Post;
import com.movie.picturas.utils.EndlessRecyclerViewScrollListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {
    public static final String TAG = "PostsFragment";

    RecyclerView rvPosts;
    protected PostsAdapter postsAdapter;
    protected List<Post> allPosts;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup any handles to view objects here
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching new data.");
                // code to refresh the list here
                queryPost();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize the list
        allPosts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), allPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Set the adapter and the linear layout manager to the RecyclerView
        rvPosts.setAdapter(postsAdapter);
        rvPosts.setLayoutManager(linearLayoutManager);

        // Define the scrollListener by passing the linearLayoutManager
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG,"onLoadMore" + page);
                loadMorePosts();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        queryPost();
    }

    // this is where we will make another query to get the next page of posts
    // and add the objects to our current list of posts
    private void loadMorePosts() {

        // Define the class we would like to query
        ParseQuery<Post> postParseQuery = ParseQuery.getQuery(Post.class);

        // Include user key
        postParseQuery.include(Post.KEY_USER);
        // Get the last post
        Post lastPost = allPosts.get(allPosts.size()-1);

        // Retrieve Posts made by only the current user
        postParseQuery.whereLessThan(Post.KEY_CREATED_AT, lastPost.getCreatedAt());

        // Limit the number of posts
        postParseQuery.setLimit(20);
        // Display the most recent posts first
        postParseQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        // Execute the find asynchronously
        postParseQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "ERROR while retrieving posts", e);
                    return;
                }

                for (Post post: posts){
                    Log.i(TAG, "Post: " + post.getDescription() + " username: " + post.getUser().getUsername());
                }
                // Append the new data objects to the existing set of items inside the array of items
                // Also notify the adapter of the new items made with `notifyItemRangeInserted()`
                allPosts.addAll(posts);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }

    // Retrieve Posts from the Parse db
    protected void queryPost() {
        // Define the class we would like to query
        ParseQuery<Post> postParseQuery = ParseQuery.getQuery(Post.class);

        // Include user key
        postParseQuery.include(Post.KEY_USER);
        // Limit the number of posts
        postParseQuery.setLimit(20);
        // Display the most recent posts first
        postParseQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        // Execute the find asynchronously
        postParseQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "ERROR while retrieving posts", e);
                    return;
                }

                for (Post post: posts){
                    Log.i(TAG, "Post: " + post.getDescription() + " username: " + post.getUser().getUsername());
                }
                // CLEAR OUT old items before appending in the new ones
                postsAdapter.clear();
                // ...the data has come back, add new items to the adapter...
                allPosts.addAll(posts);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

                postsAdapter.notifyDataSetChanged();
            }
        });
    }

}