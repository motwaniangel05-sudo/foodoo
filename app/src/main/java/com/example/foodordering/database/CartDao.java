package com.example.foodordering.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {
    @Insert
    void insert(Cart cart);

    @Update
    void update(Cart cart);

    @Delete
    void delete(Cart cart);

    @Query("SELECT * FROM cart")
    List<Cart> getAllCartItems();

    @Query("SELECT * FROM cart WHERE foodId = :foodId LIMIT 1")
    Cart getCartItemByFoodId(int foodId);

    @Query("DELETE FROM cart")
    void clearCart();
}
