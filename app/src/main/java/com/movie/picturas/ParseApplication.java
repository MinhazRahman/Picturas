package com.movie.picturas;

import android.app.Application;

import com.movie.picturas.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the parse model
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("xKJy1xWVOPtDM4jurLjcPrOJFGVXtfr5stIVTcW8")
                .clientKey("PvdoS8e8mvZ91yQQ0BK2OoWDHUtBMdGvKWjN0nuf")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
