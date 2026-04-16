package com.example.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodordering.adapter.CartAdapter;
import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.Cart;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private AppDatabase db;
    private List<Cart> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = AppDatabase.getInstance(this);

        rvCart = findViewById(R.id.rvCart);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        setupRecyclerView();
        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });
    }

    private void setupRecyclerView() {
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(null, this);
        rvCart.setAdapter(adapter);
    }

    private void loadCartItems() {
        cartList = db.cartDao().getAllCartItems();
        adapter.updateList(cartList);
        calculateTotal();
    }

    private void calculateTotal() {
        double total = 0;
        for (Cart item : cartList) {
            total += (item.price * item.quantity);
        }
        tvTotalAmount.setText("₹" + total);
    }

    @Override
    public void onQuantityChange(Cart cart, int newQty) {
        cart.quantity = newQty;
        db.cartDao().update(cart);
        loadCartItems();
    }

    @Override
    public void onDelete(Cart cart) {
        db.cartDao().delete(cart);
        loadCartItems();
        Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
    }
}
