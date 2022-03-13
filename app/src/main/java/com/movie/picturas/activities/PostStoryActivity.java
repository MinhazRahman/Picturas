package com.movie.picturas.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.movie.picturas.R;
import com.movie.picturas.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class PostStoryActivity extends AppCompatActivity {

    public static final String TAG = "PostStoryActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1020;

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    EditText etDescription;
    Button btnTakePicture;
    ImageView ivImagePreview;
    Button btnPost;
    File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_story);

        // Find the toolbar view inside the activity layout
        toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // find the views
        etDescription = findViewById(R.id.etDescription);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        btnPost = findViewById(R.id.btnPost);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set click listener on Take Picture button
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        // Retrieve posts from the database
        // queryPost();

        // Set click listener on Submit button
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the description of the post
                String description = etDescription.getText().toString().trim();

                if (description.isEmpty()){
                    Toast.makeText(PostStoryActivity.this, "Description can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoFile == null || ivImagePreview.getDrawable() == null){
                    Toast.makeText(PostStoryActivity.this, "No image to post!", Toast.LENGTH_SHORT).show();
                }

                // We also want to save the current user
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });

        // set click listener on bottom navigation
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemHome:
                        Toast.makeText(PostStoryActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemSearch:
                        Toast.makeText(PostStoryActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemVideo:
                        Toast.makeText(PostStoryActivity.this, "Video", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemShop:
                        Toast.makeText(PostStoryActivity.this, "Shop", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.itemProfile:
                        Toast.makeText(PostStoryActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        launchLogoutActivity();
                        return true;

                    default: return true;
                }
            }
        });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Toolbar menu items are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.smiStory:
                Toast.makeText(getApplicationContext(),"Add Story Selected",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.smiReel:
                Toast.makeText(getApplicationContext(),"Reel Selected",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.miFavourite:
                Toast.makeText(getApplicationContext(),"Favourite Selected",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.miMessage:
                Toast.makeText(getApplicationContext(),"Message Selected",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivImagePreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Launch the Camera
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PostStoryActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // Create and save a new post while clicking on the Submit button
    private void savePost(String description, ParseUser currentUser, File photoFile) {
        // Crate a Post object
        Post post = new Post();
        // Set attribute values
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);

        // save the post
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving post", e);
                    Toast.makeText(PostStoryActivity.this, "Error while saving post", Toast.LENGTH_SHORT).show();
                }

                Log.i(TAG, "Post saved successfully!");
                // Clear the text description field and the image view
                etDescription.setText("");
                ivImagePreview.setImageResource(0);
            }
        });
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