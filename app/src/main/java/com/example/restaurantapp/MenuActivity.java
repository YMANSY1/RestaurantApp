package com.example.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restaurantapp.databinding.ActivityMenuBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuActivity extends AppCompatActivity {
    private ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        boolean adminMode = getIntent().getBooleanExtra("adminOrUser", false);
        Log.d("TAG", "onCreate: " + adminMode);
        setContentView(binding.getRoot());
        Log.e("TAG", "onCreate: "+ReviewedMealsManager.getReviewedMeals());
        if (adminMode) binding.textView.setText("Reports");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        binding.progressBar.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            try {
                SupabaseKtClass sp = new SupabaseKtClass(this);
                List<Meals> meals = sp.fetchMealsDatabase();
                runOnUiThread(() -> {
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    MealsRecyclerViewAdapter adapter = new MealsRecyclerViewAdapter(meals,adminMode);
                    binding.recyclerView.setAdapter(adapter);
                    binding.recyclerView.addItemDecoration(new MarginItemDecoration(24));
                    binding.progressBar.setVisibility(View.INVISIBLE);

                    adapter.setOnItemClickListener((adapterView, view, i, l) -> {
                        Meals currMeal = meals.get(i);
                        Intent intent;
                        if (!adminMode) {
                            intent = new Intent(MenuActivity.this, MealReviewActivity.class);
                            binding.submitButton.setVisibility(View.VISIBLE); // Ensure submitButton is referenced correctly
                        } else {
                            intent = new Intent(MenuActivity.this, MealReportScreen.class);
                        }
                        intent.putExtra("mealObject", currMeal);
                        Toast.makeText(this, currMeal.getMeal_name(), Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    });

                    if (!adminMode) {
                        binding.submitButton.setVisibility(View.VISIBLE);
                        binding.submitButton.setOnClickListener(v->{
                            ArrayList<Float> reviewedMeals = ReviewedMealsManager.getReviewedMeals();
                            if (reviewedMeals.isEmpty()){
                                Toast.makeText(this, "Please review at least one meal", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.e("TAG", "onCreate: " +reviewedMeals);
                            float total=0F;
                            for (Float avgCurrentReview : reviewedMeals) {
                                total+=avgCurrentReview;
                            }
                            float finalAverage= total/reviewedMeals.size();
                            String message;
                            if (finalAverage==5F) message="Wow, perfect score!! You really seem to have enjoyed your stay here! Happy to have served you!";
                            else if (finalAverage < 3.0) {
                                message = "We're sorry your experience wasn't great. Your ratings have been sent to management and will be reviewed. We'll strive to provide a better experience next time!";
                            } else {
                                message = "Thank you for your positive feedback, Looking forward to seeing you next time!";
                            }

                            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();

                            ReviewedMealsManager.resetReviewedMeals();
                        });
                    } else {
                        binding.submitButton.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                Log.e("MenuActivity", "Error fetching meals database", e);
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "Error Fetching meals database", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
