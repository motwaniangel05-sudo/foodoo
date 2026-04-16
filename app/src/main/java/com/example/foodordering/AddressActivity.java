package com.example.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.User;

public class AddressActivity extends AppCompatActivity {

    private EditText etAddress;
    private Button btnSaveAddress;
    private AppDatabase db;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        db = AppDatabase.getInstance(this);
        userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        etAddress = findViewById(R.id.etAddress);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);

        String detectedAddress = getIntent().getStringExtra("detectedAddress");
        if (detectedAddress != null) {
            etAddress.setText(detectedAddress);
        }

        btnSaveAddress.setOnClickListener(v -> {
            String address = etAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            } else {
                User user = db.userDao().getUserById(userId);
                if (user != null) {
                    user.address = address;
                    db.userDao().update(user);
                    Toast.makeText(this, "Address Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddressActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
}
