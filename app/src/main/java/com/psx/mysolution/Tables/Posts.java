package com.psx.mysolution.Tables;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Pranav on 07-05-2017.
 */
@Table(name = "Posts")
public class Posts extends Model {

    @Column (name = "userId")
    public int userId;

    @Column (name = "title")
    public String title;

    @Column (name = "body")
    public String body;

    public Posts (){
        // empty constructor
        super();
    }

    public Posts (int userId, String title, String body){
        this.userId = userId;
        this.body = body;
        this.title = title;
    }
}
