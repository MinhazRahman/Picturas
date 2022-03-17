package com.movie.picturas.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.movie.picturas.R;
import com.movie.picturas.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class PostDetailActivity extends AppCompatActivity {

    RelativeLayout rlPostDetailContainer;
    Toolbar toolbarPostDetail;
    ImageView ivBackArrow;
    ImageView ivProfileImage;
    TextView tvUsername;
    TextView tvCreationTime;
    ImageView ivImage;
    TextView tvDescription;

    // Images for like, share, comment and save features
    ImageView ivLike;
    ImageView ivComments;
    ImageView ivShare;
    ImageView ivSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Find the views
        rlPostDetailContainer = findViewById(R.id.rlPostDetailContainer);
        toolbarPostDetail = findViewById(R.id.toolbarPostDetail);
        ivBackArrow = findViewById(R.id.ivBackArrow);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUsername = findViewById(R.id.tvUsername);
        tvCreationTime = findViewById(R.id.tvCreationTime);
        ivImage = findViewById(R.id.ivImage);
        tvDescription = findViewById(R.id.tvDescription);

        // Unwrap the Parcel Object
        Post post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        // Bind the post data to the view elements
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvCreationTime.setText(post.getRelativeCreationTime());

        // load image
        ParseFile image = post.getImage();
        if (image != null){
            Glide.with(this).load(image.getUrl()).into(ivImage);
        }

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
    }
}