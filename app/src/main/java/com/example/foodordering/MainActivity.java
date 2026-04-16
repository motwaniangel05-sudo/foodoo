package com.example.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodordering.adapter.RestaurantAdapter;
import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.Cart;
import com.example.foodordering.database.Food;
import com.example.foodordering.database.Restaurant;
import com.example.foodordering.database.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener {

    private RecyclerView rvRestaurants;
    private RestaurantAdapter adapter;
    private AppDatabase db;
    private TextView tvGreeting, tvLocation;
    private EditText etSearch;
    private List<Restaurant> allRestaurantList = new ArrayList<>();
    private Button btnCatAll, btnCatPizza, btnCatBurger, btnCatDrinks;
    
    private MaterialCardView cardCartStrip;
    private TextView tvCartStripCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvLocation = findViewById(R.id.tvLocation);
        etSearch = findViewById(R.id.etSearch);
        rvRestaurants = findViewById(R.id.rvFood);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        
        btnCatAll = findViewById(R.id.btnCatAll);
        btnCatPizza = findViewById(R.id.btnCatPizza);
        btnCatBurger = findViewById(R.id.btnCatBurger);
        btnCatDrinks = findViewById(R.id.btnCatDrinks);

        cardCartStrip = findViewById(R.id.cardCartStrip);
        tvCartStripCount = findViewById(R.id.tvCartStripCount);

        User user = db.userDao().getUserById(userId);
        if (user != null) {
            tvGreeting.setText("Hello, " + user.name + " 👋");
            if (user.address != null && !user.address.isEmpty()) {
                tvLocation.setText(user.address);
            } else {
                tvLocation.setText("Set your location 📍");
            }
        }
        
        tvLocation.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LocationActivity.class)));

        setupRecyclerView();
        loadData();
        setupCategoryListeners();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRestaurants(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_orders) {
                startActivity(new Intent(MainActivity.this, OrdersActivity.class));
                return true;
            }
            if (id == R.id.nav_cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            }
            if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        cardCartStrip.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
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
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(new ArrayList<>(), this);
        rvRestaurants.setAdapter(adapter);
    }

    private void loadData() {
        allRestaurantList = db.restaurantDao().getAllRestaurants();
        if (allRestaurantList.isEmpty()) {
            insertDummyData();
            allRestaurantList = db.restaurantDao().getAllRestaurants();
        }
        adapter.updateList(allRestaurantList);
    }

    private void insertDummyData() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(new Restaurant("Pizza Paradise", "Italian • Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591", "4.8"));
        restaurants.add(new Restaurant("The Burger House", "American • Burgers", "https://images.unsplash.com/photo-1571091718767-18b5b1457add", "4.5"));
        restaurants.add(new Restaurant("Healthy Greens", "Salads • Healthy", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c", "4.3"));
        restaurants.add(new Restaurant("Taco Bell", "Mexican • Tacos", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38", "4.1"));
        restaurants.add(new Restaurant("Starbucks", "Coffee • Bakery", "https://images.unsplash.com/photo-1509042239860-f550ce710b93", "4.6"));
        restaurants.add(new Restaurant("Sushi World", "Japanese • Sushi", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c", "4.7"));
        db.restaurantDao().insertAll(restaurants);

        List<Restaurant> savedRes = db.restaurantDao().getAllRestaurants();
        List<Food> foods = new ArrayList<>();
        
        // Pizza Paradise
        foods.add(new Food(savedRes.get(0).id, "Margherita Pizza", 249, "Pizza", "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3", "Classic Margherita with fresh basil."));
        foods.add(new Food(savedRes.get(0).id, "Pepperoni Pizza", 399, "Pizza", "https://images.unsplash.com/photo-1628840042765-356cda07504e", "Classic Pepperoni."));
        foods.add(new Food(savedRes.get(0).id, "Veggie Pizza", 329, "Pizza", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38", "Mixed fresh vegetables."));
        
        // Burger House
        foods.add(new Food(savedRes.get(1).id, "Cheese Burger", 199, "Burger", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", "Juicy cheese burger."));
        foods.add(new Food(savedRes.get(1).id, "Chicken Burger", 179, "Burger", "https://images.unsplash.com/photo-1550547660-d9450f859349", "Spicy chicken burger."));
        
        // Healthy Greens
        foods.add(new Food(savedRes.get(2).id, "Quinoa Salad", 149, "Salads", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd", "Healthy Quinoa."));
        foods.add(new Food(savedRes.get(2).id, "Avocado Toast", 249, "Salads", "https://images.unsplash.com/photo-1525351484163-7529414344d8", "Fresh avocado."));
        
        // Taco Bell
        foods.add(new Food(savedRes.get(3).id, "Beef Taco", 129, "Mexican", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38", "Crunchy beef taco."));
        foods.add(new Food(savedRes.get(3).id, "Chicken Burrito", 189, "Mexican", "https://images.unsplash.com/photo-1566740933430-b5e70b06d2d5", "Tasty chicken burrito."));
        
        // Starbucks
        foods.add(new Food(savedRes.get(4).id, "Iced Coffee", 259, "Drinks", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735", "Cold brewed coffee."));
        foods.add(new Food(savedRes.get(4).id, "Caramel Latte", 299, "Drinks", "https://images.unsplash.com/photo-1572490122747-3968b75cc699", "Sweet caramel flavor."));
        
        // Sushi World
        foods.add(new Food(savedRes.get(5).id, "Salmon Sushi", 449, "Japanese", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c", "Fresh salmon."));
        foods.add(new Food(savedRes.get(5).id, "California Roll", 349, "Japanese", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c", "Classic California roll."));

        db.foodDao().insertAll(foods);
    }

    private void setupCategoryListeners() {
        btnCatAll.setOnClickListener(v -> { adapter.updateList(allRestaurantList); updateCategoryUI(btnCatAll); });
        btnCatPizza.setOnClickListener(v -> { filterByCategory("Pizza"); updateCategoryUI(btnCatPizza); });
        btnCatBurger.setOnClickListener(v -> { filterByCategory("Burger"); updateCategoryUI(btnCatBurger); });
        btnCatDrinks.setOnClickListener(v -> { filterByCategory("Drinks"); updateCategoryUI(btnCatDrinks); });
    }

    private void filterByCategory(String category) {
        List<Restaurant> filtered = new ArrayList<>();
        for (Restaurant r : allRestaurantList) {
            if (r.cuisine.toLowerCase().contains(category.toLowerCase())) filtered.add(r);
        }
        adapter.updateList(filtered);
    }

    private void updateCategoryUI(Button selected) {
        btnCatAll.setTextColor(getResources().getColor(R.color.text_white));
        btnCatPizza.setTextColor(getResources().getColor(R.color.text_white));
        btnCatBurger.setTextColor(getResources().getColor(R.color.text_white));
        btnCatDrinks.setTextColor(getResources().getColor(R.color.text_white));
        selected.setTextColor(getResources().getColor(R.color.accent_orange));
    }

    private void filterRestaurants(String query) {
        List<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant r : allRestaurantList) {
            if (r.name.toLowerCase().contains(query.toLowerCase())) filteredList.add(r);
        }
        adapter.updateList(filteredList);
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("restaurantId", restaurant.id);
        startActivity(intent);
    }
}
