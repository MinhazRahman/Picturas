package com.movie.picturas.fragments;

import android.util.Log;

import com.movie.picturas.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CurrentUserPostsFragment extends PostsFragment{
    // Retrieve Posts from the Parse db
    @Override
    protected void queryPost() {
        // Define the class we would like to query
        ParseQuery<Post> postParseQuery = ParseQuery.getQuery(Post.class);

        // Include user key
        postParseQuery.include(Post.KEY_USER);
        // Retrieve Posts made by only the current user
        postParseQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

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
                allPosts.addAll(posts);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }
}
