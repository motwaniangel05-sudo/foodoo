package com.example.foodordering.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class Cart {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int foodId;
    public String name;
    public double price;
    public int quantity;

    public Cart(int foodId, String name, double price, int quantity) {
        this.foodId = foodId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
