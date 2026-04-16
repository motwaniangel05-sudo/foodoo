package com.example.foodordering.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "restaurants")
public class Restaurant {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String cuisine;
    public String imageUrl;
    public String rating;

    public Restaurant(String name, String cuisine, String imageUrl, String rating) {
        this.name = name;
        this.cuisine = cuisine;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }
}
