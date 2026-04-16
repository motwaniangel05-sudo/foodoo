package com.example.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.Cart;
import com.example.foodordering.database.Food;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivFoodDetail;
    private TextView tvFoodNameDetail, tvPriceDetail, tvDescription, tvQty;
    private ImageButton btnPlus, btnMinus;
    private Button btnAddToCartDetail;
    private AppDatabase db;
    private int quantity = 1;
    private Food currentFood;

    // Cart Strip
    private MaterialCardView cardCartStrip;
    private TextView tvCartStripCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = AppDatabase.getInstance(this);
        int foodId = getIntent().getIntExtra("foodId", -1);
        currentFood = db.foodDao().getFoodById(foodId);

        ivFoodDetail = findViewById(R.id.ivFoodDetail);
        tvFoodNameDetail = findViewById(R.id.tvFoodNameDetail);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        tvDescription = findViewById(R.id.tvDescription);
        tvQty = findViewById(R.id.tvQty);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnAddToCartDetail = findViewById(R.id.btnAddToCartDetail);
        
        cardCartStrip = findViewById(R.id.cardCartStrip);
        tvCartStripCount = findViewById(R.id.tvCartStripCount);

        if (currentFood != null) {
            tvFoodNameDetail.setText(currentFood.name);
            tvPriceDetail.setText("₹" + currentFood.price);
            tvDescription.setText(currentFood.description);
            Glide.with(this).load(currentFood.imageUrl).into(ivFoodDetail);
        }

        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQty.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQty.setText(String.valueOf(quantity));
            }
        });

        btnAddToCartDetail.setOnClickListener(v -> {
            Cart existingItem = db.cartDao().getCartItemByFoodId(currentFood.id);
            if (existingItem != null) {
                existingItem.quantity += quantity;
                db.cartDao().update(existingItem);
            } else {
                db.cartDao().insert(new Cart(currentFood.id, currentFood.name, currentFood.price, quantity));
            }
            updateCartStrip();
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        cardCartStrip.setOnClickListener(v -> {
            startActivity(new Intent(DetailActivity.this, CartActivity.class));
        });
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
}
