package com.example.foodordering;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodordering.adapter.OrderAdapter;
import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.Order;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private AppDatabase db;
    private TextView tvNoOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        db = AppDatabase.getInstance(this);
        rvOrders = findViewById(R.id.rvOrders);
        
        setupRecyclerView();
        loadOrders();
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(null);
        rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        List<Order> orders = db.orderDao().getAllOrders();
        if (orders == null || orders.isEmpty()) {
            // You could show a "No Orders" text here if you add it to the layout
        } else {
            adapter.updateList(orders);
        }
    }
}
