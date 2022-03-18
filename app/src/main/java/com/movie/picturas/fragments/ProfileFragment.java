package com.movie.picturas.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.movie.picturas.R;
import com.movie.picturas.activities.LoginActivity;
import com.movie.picturas.activities.LogoutActivity;
import com.movie.picturas.adapters.ProfileAdapter;
import com.movie.picturas.models.Post;
import com.movie.picturas.utils.SpacesItemDecoration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    RecyclerView rvPosts;
    ProfileAdapter profileAdapter;
    List<Post> allPosts;
    ImageView ivProfileImage;
    TextView tvUsername;
    Button btnLogout;
    SpacesItemDecoration spacesItemDecoration;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // find views in the layout
        rvPosts = view.findViewById(R.id.rvCurrentUserPosts);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Initialize the list
        allPosts = new ArrayList<>();
        profileAdapter = new ProfileAdapter(getContext(), allPosts);


        // Create the layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);

        // attach a decorator to  the recyclerview for adding consistent spacing around items displayed
        spacesItemDecoration = new SpacesItemDecoration(5);

        // Set the adapter and the layout manager to the RecyclerView
        rvPosts.setAdapter(profileAdapter);
        rvPosts.setLayoutManager(gridLayoutManager);
        rvPosts.addItemDecoration(spacesItemDecoration);

        // set click listener on log out button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log out the user
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

                Toast.makeText(getContext(), "Logged out!", Toast.LENGTH_SHORT).show();

                // Navigate to login activity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // when clicked on recycler view, show the current user posts
        rvPosts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        // Bind all the data
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profileImage");
        assert profileImage != null;
        Glide.with(this).load(profileImage.getUrl()).into(ivProfileImage);


        // Call queryPost() method to get the data
        queryPost();
    }

    // Retrieve all the posts for current logged in user from the Parse DB
    void queryPost() {
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
                profileAdapter.notifyDataSetChanged();
            }
        });
    }
}