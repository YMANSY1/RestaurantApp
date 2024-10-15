package com.example.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantapp.databinding.ActivityMealReviewScreenBinding;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMealReviewScreenBinding binding = ActivityMealReviewScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Meals currMeal= getIntent().getParcelableExtra("mealObject");

        assert currMeal != null;
        List<String> ingredients= currMeal.getIngredients();

        Picasso.get()
                .load(currMeal.getMeal_image())
                .into(binding.mealImage);
        binding.mealName.setText(currMeal.getMeal_name());

        for (String ingredient : ingredients) {
            addIngredientWithRatingBar(binding,ingredient);
        }
        binding.submitButton.setOnClickListener(v -> {
            String suggestion = binding.suggestionText.getText().toString();
            if (!suggestion.isEmpty()) currMeal.getUser_comments().add(suggestion);
            ArrayList<Float> ingredientRatings = new ArrayList<>();
            for (int i=0;i<ingredients.size();i++) {
                View ingredientView=binding.ingredientsContainer.getChildAt(i);
                RatingBar ratingBar= ingredientView.findViewById(R.id.rating_bar);
                float rating= ratingBar.getRating();
                ingredientRatings.add(rating);
            }
            if (RatingsEmpty(ingredientRatings)){
                Toast.makeText(this, "Please rate at least one ingredient", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("TAG", "onCreate: "+ingredientRatings+" "+ currMeal.getUser_comments());

            //Upload to database
            ExecutorService executorService= Executors.newSingleThreadExecutor();
            executorService.execute(()->{
                try {
                    SupabaseKtClass sp = new SupabaseKtClass(this);
                    sp.updateCommentsInTable(currMeal.getUser_comments(),currMeal.getId());
                    sp.updateRatingsAverage(ingredientRatings,currMeal.getId());
                }
                catch (Exception e){
                    Log.e("TAG", "onCreate: error updating");
                }
            });
            ReviewedMealsManager.addReviewedMeal(calcAverage(ingredientRatings));
            //Go back to menu screen
            Intent intent= new Intent(MealReviewActivity.this,MenuActivity.class);
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
    }

    private void addIngredientWithRatingBar(ActivityMealReviewScreenBinding binding, String ingredient) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View ingredientView = inflater.inflate(R.layout.ingredient_item, binding.ingredientsContainer, false); // use ingredientsContainer

        TextView ingredientName = ingredientView.findViewById(R.id.ingredient_name);
        ingredientName.setText(ingredient);
        binding.ingredientsContainer.addView(ingredientView); // use ingredientsContainer
    }

    boolean RatingsEmpty(List<Float> ratings){
        for (Float rating : ratings) {
            if (rating!=0.0) return false;
        }
        return true;
    }
    float calcAverage(List<Float> ratings) {
        double total = 0;
        int nonZero = 0;

        for (Float rating : ratings) {
            if (rating >= 1.0f && rating <= 5.0f) {  // Only count valid ratings
                nonZero++;
                total += rating;
            }
        }

        if (nonZero == 0) {
            Log.e("calcAverage", "No valid ratings found, returning 0.0f");
            return 0.0f;
        }

        double average = total / nonZero;
        Log.d("calcAverage", "Total: " + total + ", Non-zero count: " + nonZero + ", Average: " + average);
        return (float) average;
    }
}
