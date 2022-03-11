package com.movie.picturas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.movie.picturas.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    EditText etDescription;
    Button btnTakePicture;
    ImageView ivPostImage;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the views
        etDescription = findViewById(R.id.etDescription);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        ivPostImage = findViewById(R.id.ivPostImage);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Retrieve posts from the database
        // queryPost();

        // Create and save a new post while clicking on the Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the description of the post
                String description = etDescription.getText().toString().trim();

                if (description.isEmpty()){
                    Toast.makeText(MainActivity.this, "Description can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // We also want to save the current user
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser);
            }
        });
    }

    private void savePost(String description, ParseUser currentUser) {
        // Crate a Post object
        Post post = new Post();
        // Set attribute values
        post.setDescription(description);
        //post.setImage();
        post.setUser(currentUser);

        // save the post
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving post", e);
                    Toast.makeText(MainActivity.this, "Error while saving post", Toast.LENGTH_SHORT).show();
                }

                Log.i(TAG, "Post saved successfully!");
                etDescription.setText("");
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
}