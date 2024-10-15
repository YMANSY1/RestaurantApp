package com.example.restaurantapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<String> ingredientsList;
    private List<Float> ratingsList;

    public IngredientAdapter(List<String> ingredientsList, List<Float> ratingsList) {
        this.ingredientsList = ingredientsList;
        this.ratingsList = ratingsList;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        String ingredient = ingredientsList.get(position);
        float rating = ratingsList.get(position);

        holder.ingredientTextView.setText(ingredient);
        holder.ratingBar.setRating(rating);
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientTextView;
        RatingBar ratingBar;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientTextView = itemView.findViewById(R.id.ingredient_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}

