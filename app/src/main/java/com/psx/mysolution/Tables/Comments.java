package com.psx.mysolution.Tables;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/*
 * Created by Pranav on 07-05-2017.
 */
@Table(name="Comments")
public class Comments extends Model {
    @Column(name = "postId")
    public int postId;

    @Column(name = "name")
    public String name;

    @Column(name = "email")
    public String email;

    @Column(name = "body")
    public String body;

    public Comments () {
        // empty constructor
        super();
    }

    public Comments (int postId, String name, String email, String body) {
        super();
        this.postId = postId;
        this.name = name;
        this.body = body;
        this.email = email;
    }
}
