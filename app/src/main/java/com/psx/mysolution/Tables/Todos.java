package com.psx.mysolution.Tables;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Pranav on 07-05-2017.
 */

@Table(name = "Todos")
public class Todos extends Model {

    @Column(name = "userId")
    public int userId;

    @Column(name = "title")
    public String title;

    @Column(name = "completed")
    public boolean completed;

    public Todos (){
        // empty constructor
        super();
    }

    public Todos (int userId, String title, boolean completed){
        this.userId = userId;
        this.title = title;
        this.completed = completed;
    }

}
