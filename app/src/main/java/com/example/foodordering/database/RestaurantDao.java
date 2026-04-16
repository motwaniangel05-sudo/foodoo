package com.example.foodordering.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestaurantDao {
    @Insert
    void insertAll(List<Restaurant> restaurants);

    @Query("SELECT * FROM restaurants")
    List<Restaurant> getAllRestaurants();

    @Query("SELECT * FROM restaurants WHERE id = :id LIMIT 1")
    Restaurant getRestaurantById(int id);
}
