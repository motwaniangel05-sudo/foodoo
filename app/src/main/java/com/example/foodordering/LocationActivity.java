package com.example.foodordering;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.foodordering.database.AppDatabase;
import com.example.foodordering.database.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private AppDatabase db;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        db = AppDatabase.getInstance(this);
        userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button btnAllowLocation = findViewById(R.id.btnAllowLocation);
        TextView tvSkip = findViewById(R.id.tvSkip);

        btnAllowLocation.setOnClickListener(v -> requestLocationPermission());

        tvSkip.setOnClickListener(v -> {
            startActivity(new Intent(LocationActivity.this, MainActivity.class));
            finish();
        });
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Priority.PRIORITY_HIGH_ACCURACY ensures a fresh location is requested if the last one is null
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        getAddressFromLocation(location);
                    } else {
                        // Fallback to last known location if getCurrentLocation fails
                        fusedLocationClient.getLastLocation().addOnSuccessListener(this, lastLoc -> {
                            if (lastLoc != null) {
                                getAddressFromLocation(lastLoc);
                            } else {
                                Toast.makeText(this, "Could not fetch location. Please ensure GPS is ON.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1, addresses -> {
                if (addresses != null && !addresses.isEmpty()) {
                    runOnUiThread(() -> processAddress(addresses.get(0)));
                }
            });
        } else {
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    processAddress(addresses.get(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void processAddress(Address address) {
        String addressStr = address.getAddressLine(0);
        
        // Update address in DB
        User user = db.userDao().getUserById(userId);
        if (user != null) {
            user.address = addressStr;
            db.userDao().update(user);
        }
        
        Intent intent = new Intent(LocationActivity.this, AddressActivity.class);
        intent.putExtra("detectedAddress", addressStr);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied. You can enter address manually.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
