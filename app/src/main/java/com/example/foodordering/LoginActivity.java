package com.example.foodordering;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private CheckBox cbRememberMe;
    private AppDatabase db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDatabase.getInstance(this);
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        // Check if Remember Me was previously checked
        if (prefs.getBoolean("rememberMe", false)) {
            etEmail.setText(prefs.getString("savedEmail", ""));
            etPassword.setText(prefs.getString("savedPassword", ""));
            cbRememberMe.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                User user = db.userDao().login(email, password);
                if (user != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("userId", user.id);
                    
                    if (cbRememberMe.isChecked()) {
                        editor.putBoolean("rememberMe", true);
                        editor.putString("savedEmail", email);
                        editor.putString("savedPassword", password);
                    } else {
                        editor.putBoolean("rememberMe", false);
                        editor.remove("savedEmail");
                        editor.remove("savedPassword");
                    }
                    editor.apply();

                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    
                    // Navigate based on whether address exists
                    if (user.address != null && !user.address.isEmpty()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, LocationActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }
}
