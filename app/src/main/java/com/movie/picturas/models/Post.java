package com.movie.picturas.models;

import com.movie.picturas.utils.TimeFormatter;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Using with ORM libraries
 * Some ORM libraries require extending the Java object with fields that Parceler is unable to serialize or deserialize.
 * In these, cases, you should limit what fields should be analyzed in the inheritance using the @Parcel(analyze={}) decorator:
 *
 * @Parcel(analyze={User.class})   // add Parceler annotation here
 * public class User extends BaseModel {
 * }
 *
 * In this case only parameters from User class will be serialized avoiding any fields from BaseModel.
* */


@ParseClassName("Post")
@Parcel(analyze={Post.class})
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";

    // empty constructor needed by the Parceler library
    public Post(){
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public String getRelativeCreationTime(){
        return ". " + TimeFormatter.getTimeDifference(String.valueOf(getCreatedAt()));
    }

    public void setCreationTime(Date date){
        put(KEY_CREATED_AT, date);
    }
}
