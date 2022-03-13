package com.movie.picturas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.movie.picturas.R;
import com.movie.picturas.fragments.PostStoryFragment;
import com.movie.picturas.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout flContainer;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar view inside the activity layout
        toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // find the views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        flContainer = findViewById(R.id.flContainer);

        // Retrieve posts from the database
        // queryPost();

        // set click listener on bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemHome:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemSearch:
                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemVideo:
                        Toast.makeText(MainActivity.this, "Video", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemShop:
                        Toast.makeText(MainActivity.this, "Shop", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemProfile:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        launchLogoutActivity();
                        return true;

                    default: return true;
                }
            }
        });

        // Set default selection, in this case home item
        bottomNavigationView.setSelectedItemId(R.id.itemHome);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.smiStory:
                Toast.makeText(getApplicationContext(),"Add Story Selected",Toast.LENGTH_SHORT).show();
                // Navigate to the PostStoryActivity
                //Intent intent = new Intent(this, PostStoryActivity.class);
                //startActivity(intent);
                fragment = new PostStoryFragment();
                break;
            case R.id.smiReel:
                Toast.makeText(getApplicationContext(),"Reel Selected",Toast.LENGTH_SHORT).show();
                fragment = new PostStoryFragment();
                break;
            case R.id.miFavourite:
                Toast.makeText(getApplicationContext(),"Favourite Selected",Toast.LENGTH_SHORT).show();
                fragment = new PostStoryFragment();
                break;
            case R.id.miMessage:
            default:
                Toast.makeText(getApplicationContext(),"Message Selected",Toast.LENGTH_SHORT).show();
                fragment = new PostStoryFragment();
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        return true;
    }

    private void queryPost() {
        // Define the class we would like to query
        ParseQuery<Post> postParseQuery = ParseQuery.getQuery(Post.class);

        // Include user key
        postParseQuery.include(Post.KEY_USER);
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
            }
        });
    }

    private void launchLogoutActivity(){
        // Navigate to logout activity
        Intent intent = new Intent(this, LogoutActivity.class);
        startActivity(intent);
    }
}