package com.example.foodordering.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public double totalAmount;
    public long date;
    public String address;

    public Order(double totalAmount, long date, String address) {
        this.totalAmount = totalAmount;
        this.date = date;
        this.address = address;
    }
}
