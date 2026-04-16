package com.example.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodordering.adapter.FoodAdapter;
import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.Cart;
import com.example.foodordering.database.Food;
import com.example.foodordering.database.Restaurant;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity implements FoodAdapter.OnFoodClickListener {

    private ImageView ivResDetail;
    private TextView tvResNameDetail;
    private RecyclerView rvRestaurantFood;
    private FoodAdapter adapter;
    private AppDatabase db;
    private int restaurantId;

    // Cart Strip
    private MaterialCardView cardCartStrip;
    private TextView tvCartStripCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        db = AppDatabase.getInstance(this);
        restaurantId = getIntent().getIntExtra("restaurantId", -1);

        ivResDetail = findViewById(R.id.ivResDetail);
        tvResNameDetail = findViewById(R.id.tvResNameDetail);
        rvRestaurantFood = findViewById(R.id.rvRestaurantFood);
        
        cardCartStrip = findViewById(R.id.cardCartStrip);
        tvCartStripCount = findViewById(R.id.tvCartStripCount);

        Restaurant restaurant = db.restaurantDao().getRestaurantById(restaurantId);
        if (restaurant != null) {
            tvResNameDetail.setText(restaurant.name);
            Glide.with(this).load(restaurant.imageUrl).into(ivResDetail);
        }

        setupRecyclerView();
        loadRestaurantFood();

        cardCartStrip.setOnClickListener(v -> startActivity(new Intent(RestaurantDetailActivity.this, CartActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartStrip();
    }

    private void updateCartStrip() {
        List<Cart> cartItems = db.cartDao().getAllCartItems();
        if (cartItems != null && !cartItems.isEmpty()) {
            cardCartStrip.setVisibility(View.VISIBLE);
            int count = 0;
            double total = 0;
            for (Cart item : cartItems) {
                count += item.quantity;
                total += (item.price * item.quantity);
            }
            tvCartStripCount.setText(count + " Item" + (count > 1 ? "s" : "") + " | ₹" + (int)total);
        } else {
            cardCartStrip.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        rvRestaurantFood.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodAdapter(null, this);
        rvRestaurantFood.setAdapter(adapter);
    }

    private void loadRestaurantFood() {
        List<Food> foods = db.foodDao().getFoodByRestaurant(restaurantId);
        adapter.updateList(foods);
    }

    @Override
    public void onAddToCart(Food food) {
        Cart existingItem = db.cartDao().getCartItemByFoodId(food.id);
        if (existingItem != null) {
            existingItem.quantity += 1;
            db.cartDao().update(existingItem);
        } else {
            db.cartDao().insert(new Cart(food.id, food.name, food.price, 1));
        }
        updateCartStrip();
        Toast.makeText(this, food.name + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFoodClick(Food food) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("foodId", food.id);
        startActivity(intent);
    }
}
