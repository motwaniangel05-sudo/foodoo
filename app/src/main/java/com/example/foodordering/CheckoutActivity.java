package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.Cart;
import com.example.foodordering.database.Order;
import com.example.foodordering.database.User;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvCheckoutAddress, tvCheckoutTotal;
    private Button btnPlaceOrder;
    private LinearLayout loadingLayout;
    private ConstraintLayout checkoutContent;
    private AppDatabase db;
    private int userId;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = AppDatabase.getInstance(this);
        userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        tvCheckoutAddress = findViewById(R.id.tvCheckoutAddress);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        loadingLayout = findViewById(R.id.loadingLayout);
        checkoutContent = findViewById(R.id.checkoutContent);

        User user = db.userDao().getUserById(userId);
        if (user != null && user.address != null && !user.address.isEmpty()) {
            tvCheckoutAddress.setText(user.address);
        } else {
            tvCheckoutAddress.setText("No address found! Tap to set.");
            tvCheckoutAddress.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }

        calculateTotal();

        btnPlaceOrder.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Please turn on internet to place order!", Toast.LENGTH_LONG).show();
                return;
            }

            String address = tvCheckoutAddress.getText().toString();
            if (address.contains("No address found") || address.isEmpty()) {
                Toast.makeText(this, "Please set a delivery address first!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LocationActivity.class));
                return;
            }

            placeOrder();
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void placeOrder() {
        btnPlaceOrder.setEnabled(false);
        checkoutContent.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // Play sound
                try {
                    MediaPlayer mp = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
                    if (mp != null) {
                        mp.start();
                        mp.setOnCompletionListener(MediaPlayer::release);
                    }
                } catch (Exception ignored) {}

                // Save Order
                Order newOrder = new Order(totalAmount, System.currentTimeMillis(), tvCheckoutAddress.getText().toString());
                db.orderDao().insert(newOrder);
                
                // Clear Cart
                db.cartDao().clearCart();
                
                // Start Success Activity
                Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                startActivity(intent);
                finish(); // Close Checkout
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingLayout.setVisibility(View.GONE);
                    checkoutContent.setVisibility(View.VISIBLE);
                    btnPlaceOrder.setEnabled(true);
                });
            }
        }, 2000);
    }

    private void calculateTotal() {
        List<Cart> cartItems = db.cartDao().getAllCartItems();
        totalAmount = 0;
        if (cartItems != null) {
            for (Cart item : cartItems) {
                totalAmount += (item.price * item.quantity);
            }
        }
        tvCheckoutTotal.setText("₹" + (int)totalAmount);
    }
}
