package com.example.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail, tvProfileAddress;
    private Button btnLogout;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = AppDatabase.getInstance(this);
        int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);
        btnLogout = findViewById(R.id.btnLogout);

        User user = db.userDao().getUserById(userId);
        if (user != null) {
            tvProfileName.setText(user.name);
            tvProfileEmail.setText(user.email);
            tvProfileAddress.setText(user.address != null ? user.address : "No address set");
        }

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
