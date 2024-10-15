package com.example.restaurantapp;

import java.util.ArrayList;

public class ReviewedMealsManager {
    private static ArrayList<Float> reviewedMeals = new ArrayList<>();
    private ReviewedMealsManager(){}

    public static ArrayList<Float> getReviewedMeals() {
        return reviewedMeals;
    }

    public static void addReviewedMeal(Float avg){
        reviewedMeals.add(avg);
    }

    public static synchronized void resetReviewedMeals(){
        reviewedMeals= new ArrayList<>();
    }
}
