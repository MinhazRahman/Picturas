package com.movie.picturas.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.movie.picturas.R;
import com.movie.picturas.adapters.ProfileAdapter;
import com.movie.picturas.models.Post;
import com.movie.picturas.utils.SpacesItemDecoration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = "UserProfileFragment";

    RecyclerView rvPosts;
    ProfileAdapter profileAdapter;
    List<Post> allPosts;
    ImageView ivProfileImage;
    TextView tvUsername;
    SpacesItemDecoration spacesItemDecoration;
    ImageView ivBackArrow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Setup any handles to view objects here
        // find views in the layout
        rvPosts = findViewById(R.id.rvCurrentUserPosts);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUsername = findViewById(R.id.tvUsername);
        ivBackArrow = findViewById(R.id.ivBackArrow);

        // Initialize the list
        allPosts = new ArrayList<>();
        profileAdapter = new ProfileAdapter(this, allPosts);

        // Create the layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        // attach a decorator to  the recyclerview for adding consistent spacing around items displayed
        spacesItemDecoration = new SpacesItemDecoration(5);

        // Set the adapter and the layout manager to the RecyclerView
        rvPosts.setAdapter(profileAdapter);
        rvPosts.setLayoutManager(gridLayoutManager);
        rvPosts.addItemDecoration(spacesItemDecoration);

        // Unwrap the Parcel Object
        Post post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));
        // Bind the post data to the view elements
        tvUsername.setText(post.getUser().getUsername());

        // load profile image
        ParseFile profileImage = post.getUser().getParseFile("profileImage");
        if (profileImage !=null){
            Glide.with(this).load(profileImage.getUrl()).into(ivProfileImage);
        }

        // Set click listener to back arrow
        // When clicked on the Back Arrow go back to the All posts
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // closes the activity
            }
        });

        // Call queryPost() method to get the data
        queryPost(post.getUser());

    }

    // Retrieve all the posts for the user from the Parse DB
    void queryPost(ParseUser user) {
        // Define the class we would like to query
        ParseQuery<Post> postParseQuery = ParseQuery.getQuery(Post.class);
        // Notes: to get the posts with user name
        // Include user key
        postParseQuery.include(Post.KEY_USER);
        // Retrieve Posts made by only the current user
        postParseQuery.whereEqualTo(Post.KEY_USER, user);

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
                profileAdapter.notifyDataSetChanged();
            }
        });
    }
}