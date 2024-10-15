package com.example.restaurantapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.databinding.MenuItemLayoutBinding;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class MealsRecyclerViewAdapter extends RecyclerView.Adapter<MealsRecyclerViewAdapter.ViewHolder> {
    private List<Meals> mealsList;
    private AdapterView.OnItemClickListener onItemClickListener;
    private boolean adminMode;
    private Float overallRating;// Add adminMode as a field

    // Modify constructor to accept adminMode
    public MealsRecyclerViewAdapter(List<Meals> mealsList, boolean adminMode) {
        this.mealsList = mealsList;
        this.adminMode = adminMode;
    }

    // Getter and setter for adminMode if you need them
    public boolean isAdminMode() {
        return adminMode;
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
        notifyDataSetChanged();  // Refresh the data when adminMode is updated
    }

    public List<Meals> getMealsList() {
        return mealsList;
    }

    public void setMealsList(List<Meals> mealsList) {
        this.mealsList = mealsList;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MealsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(MenuItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull MealsRecyclerViewAdapter.ViewHolder holder, int position) {
        Meals meal = mealsList.get(position);

        // Only show the average rating if adminMode is true
        if (adminMode) {
            DecimalFormat numberFormat = new DecimalFormat("#.0");
            Float total=0F;
            for (Float ingredientReview : meal.getIngredient_reviews()) {
                total+=ingredientReview;
            }
            overallRating=total/meal.getIngredient_reviews().size();
            holder.binding.averageRating.setVisibility(View.VISIBLE);
            holder.binding.reviewNum.setVisibility(View.VISIBLE);
            holder.binding.star.setVisibility(View.VISIBLE);
            holder.binding.averageRating.setText(""+numberFormat.format(overallRating));
            holder.binding.reviewNum.setText("("+meal.getReview_num()+")");
        } else {
            holder.binding.averageRating.setVisibility(View.GONE);
            holder.binding.star.setVisibility(View.GONE);
            holder.binding.reviewNum.setVisibility(View.GONE);
        }

        holder.binding.mealName.setText(meal.getMeal_name());
        holder.binding.mealDescription.setText(meal.getDescription());
        holder.binding.price.setText(meal.getMeal_price() + " EGP");

        Picasso.get()
                .load(meal.getMeal_image())
                .into(holder.binding.mealImage);
    }

    @Override
    public int getItemCount() {
        return mealsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MenuItemLayoutBinding binding;

        public ViewHolder(MenuItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(null, view, getAdapterPosition(), getItemId());
            }
        }
    }
}
