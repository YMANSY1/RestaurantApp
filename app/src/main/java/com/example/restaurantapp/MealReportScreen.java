package com.example.restaurantapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.databinding.ActivityMealReportScreenBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealReportScreen extends AppCompatActivity {

    private RecyclerView ingredientsRecyclerView, commentsRecyclerView;
    private CommentAdapter commentsAdapter;
    ActivityMealReportScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMealReportScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Meals currMeal = getIntent().getParcelableExtra("mealObject");
        assert currMeal != null;
        Log.d("TAG", "onCreate: " + currMeal.getDescription());


        binding.textView.setText(currMeal.getMeal_name());
        binding.reviewNumber.setText(currMeal.getReview_num()+" reviews");
        Picasso.get()
                .load(currMeal.getMeal_image())
                .into(binding.mealImage);

        ingredientsRecyclerView = binding.recyclerView;
        commentsRecyclerView = binding.recyclerView2;

        List<String> ingredients = currMeal.getIngredients();
        List<Float> ratings = currMeal.getIngredient_reviews();
        IngredientAdapter ingredientsAdapter = new IngredientAdapter(ingredients, ratings);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        List<String> comments = currMeal.getUser_comments();

        commentsAdapter = new CommentAdapter(comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }
}




