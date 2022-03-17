package com.movie.picturas.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.movie.picturas.R;
import com.movie.picturas.activities.PostDetailActivity;
import com.movie.picturas.models.Post;
import com.movie.picturas.utils.TimeFormatter;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    Context context;
    List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    // clear() and addAll() methods are for SwipeRefreshLayout
    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items:tweets
    public void addAll(List<Post> listTweet) {
        posts.addAll(listTweet);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout rlPostContainer;
        ImageView ivProfileImage;
        TextView tvUsername;
        TextView tvCreationTime;
        ImageView ivImage;
        TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rlPostContainer = itemView.findViewById(R.id.rlPostContainer);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCreationTime = itemView.findViewById(R.id.tvCreationTime);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvCreationTime.setText(post.getRelativeCreationTime());

            // load image
            ParseFile image = post.getImage();
            if (image != null){
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            // load profile image
            ParseFile profileImage = post.getUser().getParseFile("profileImage");
            if (profileImage !=null){
                Glide.with(context).load(profileImage.getUrl()).into(ivProfileImage);
            }

            // Register click listener on whole row, that represents a post
            // When clicked on a post, it takes the user to the PostDetail screen
            rlPostContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create Intent object
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    // Wrap Post object with Parcels.wrap()
                    intent.putExtra("post", Parcels.wrap(post));
                    // Launch PostDetailActivity screen
                    context.startActivity(intent);
                }
            });
        }
    }
}
