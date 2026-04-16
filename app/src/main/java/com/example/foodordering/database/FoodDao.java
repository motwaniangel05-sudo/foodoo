package com.example.foodordering.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodDao {
    @Insert
    void insertAll(List<Food> foods);

    @Query("SELECT * FROM food")
    List<Food> getAllFood();

    @Query("SELECT * FROM food WHERE category = :category")
    List<Food> getFoodByCategory(String category);

    @Query("SELECT * FROM food WHERE id = :id LIMIT 1")
    Food getFoodById(int id);

    @Query("SELECT * FROM food WHERE restaurantId = :restaurantId")
    List<Food> getFoodByRestaurant(int restaurantId);
}
