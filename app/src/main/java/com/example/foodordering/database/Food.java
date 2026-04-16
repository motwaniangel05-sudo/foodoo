package com.example.foodordering.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "food",
        foreignKeys = @ForeignKey(entity = Restaurant.class,
                parentColumns = "id",
                childColumns = "restaurantId",
                onDelete = ForeignKey.CASCADE))
public class Food {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int restaurantId;
    public String name;
    public double price;
    public String category;
    public String imageUrl;
    public String description;

    public Food(int restaurantId, String name, double price, String category, String imageUrl, String description) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
