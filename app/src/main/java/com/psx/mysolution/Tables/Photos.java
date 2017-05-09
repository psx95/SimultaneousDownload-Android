    package com.psx.mysolution.Tables;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Pranav on 07-05-2017.
 */

@Table(name = "Photos")
public class Photos extends Model{


    /*@Column(name = "id")
    public int id;*/

    @Column(name = "albumnId")
    public int albumnId;

    @Column(name = "title")
    public String title;

    @Column(name = "url")
    public String url;

    @Column(name = "thumbnailUrl")
    public String thumbnailUrl;

    public Photos ()
    {
        // empty constructor
        super();
    }

    /*public Photos (int id, int albumnId, String title, String url, String thumbnailUrl){
        this.id = id;
        this.albumnId = albumnId;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.url = url;
    }*/
    public Photos (int albumnId, String title, String url, String thumbnailUrl){
     //   this.id = id;
        this.albumnId = albumnId;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.url = url;
    }
}
