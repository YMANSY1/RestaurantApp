package com.example.restaurantapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantapp.databinding.ActivityMainBinding;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonViewMenu.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            boolean adminMode=false;
            intent.putExtra("adminOrUser", adminMode);
            startActivity(intent);
        });
        binding.buttonViewReports.setOnClickListener(view->{
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            boolean adminMode= true;
            intent.putExtra("adminOrUser", adminMode);
            startActivity(intent);
        });
    }

    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        finishAffinity();
    }
}