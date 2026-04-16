package com.example.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Automatically redirect to MainActivity after 3 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class);
                // Clear all previous activities and start MainActivity fresh
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 3500); // 3.5 seconds delay to let the animation play
    }

    @Override
    public void onBackPressed() {
        // Disable back button during success screen to prevent going back to checkout
    }
}
