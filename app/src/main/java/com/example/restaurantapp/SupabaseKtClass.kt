package com.example.restaurantapp

import android.content.Context
import android.util.Log
import android.util.Log.d
import android.util.Log.e
import androidx.core.content.ContextCompat.getString
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class SupabaseKtClass(context: Context) {
    val SUPABASE_URL = context.getString(R.string.base_url)
    val SUPABASE_KEY = context.getString(R.string.supabase_key)

    private fun supabaseClientCreator(supabaseUrl: String, supabaseKey: String): SupabaseClient {
        return createSupabaseClient(supabaseUrl, supabaseKey) {
            install(Auth)
            install(Postgrest)
            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
                prettyPrint = true
            })
        }
    }

    fun fetchMealsDatabase(): List<Meals> {
        val supabase = supabaseClientCreator(SUPABASE_URL, SUPABASE_KEY)
        var mealsList: List<Meals> = emptyList()
        val executorService = Executors.newSingleThreadExecutor()
        val futureTask = FutureTask {
            runBlocking {
                try {
                    val response =
                        supabase.postgrest["Meals_duplicate2"].select().decodeList<Meals>()
                    mealsList = response
                    e("TAG", "fetchMealsDatabase: $response")
                } catch (e: Exception) {
                    e("TAG", "Error fetching meals database", e)
                }
            }
        }
        executorService.execute(futureTask)
        try {
            futureTask.get() // Waits for the task to complete and returns the result
        } catch (e: Exception) {
            e("TAG", "Error waiting for future task", e)
        } finally {
            executorService.shutdown()
        }
        return mealsList
    }

    fun updateCommentsInTable(userComments: List<String>, mealId: Int) {
        val supabase = supabaseClientCreator(SUPABASE_URL, SUPABASE_KEY)
        val executorService = Executors.newSingleThreadExecutor()
        val futureTask = FutureTask {
            runBlocking {
                try {
                    val meal = supabase.from("Meals_duplicate2").select {
                        filter {
                            eq("id", mealId)
                        }
                    }.decodeSingle<Meals>()
                    val updatedComments = meal.user_comments.toMutableSet().apply {
                        addAll(userComments)
                    }.toList()

                    Log.d("TAG", "Existing comments: ${meal.user_comments}")
                    Log.d("TAG", "New comments: $userComments")
                    Log.d("TAG", "Updated comments: $updatedComments")

                    supabase.from("Meals_duplicate2").update(
                        {
                            set("user_comments", updatedComments)
                        }
                    ) {
                        filter {
                            eq("id", mealId)
                        }
                    }.decodeSingle<Meals>()

                } catch (e: Exception) {
                    e("TAG", "updateCommentsInTable: couldn't update data", e)
                }
            }
        }
        executorService.execute(futureTask)
        try {
            futureTask.get()
        } catch (e: Exception) {
            e("TAG", "Error waiting for future task", e)
        } finally {
            executorService.shutdown()
        }
    }

    fun updateRatingsAverage(ratings: MutableList<Float>, mealId: Int) {
        val supabase = supabaseClientCreator(SUPABASE_URL, SUPABASE_KEY)
        val executorService = Executors.newSingleThreadExecutor()
        val futureTask = FutureTask {
            runBlocking {
                try {
                    val meal = supabase.from("Meals_duplicate2").select {
                        filter {
                            eq("id", mealId)
                        }
                    }.decodeSingle<Meals>()
//                    supabase.from("Meals_duplicate2").update(
//                        {
//                            set("latest_review", ratings)
//                        }
//                    ) {
//                        filter {
//                            eq("id", mealId)
//                        }
//                    }.decodeSingle<Meals>()
                    if (meal.ingredient_reviews.isEmpty()) supabase.from("Meals_duplicate2").update(
                        {
                            set("ingredient_reviews", ratings)
                            set("review_num",++meal.review_num)
                        }
                    ) {
                        filter {
                            eq("id", mealId)
                        }
                    }.decodeSingle<Meals>()
                    else {
                        for (i in meal.ingredient_reviews.indices) {
                            if (meal.ingredient_reviews[i]==0.0f){
                                meal.ingredient_reviews[i]=ratings[i]
                                continue
                            }
                            if (ratings[i] == 0.0f) ratings[i] = 3.0f
                            meal.ingredient_reviews[i] =
                                (meal.ingredient_reviews[i] * meal.review_num.toFloat() + ratings[i]) / (meal.review_num + 1)
                        }
                        d("TAG", "updateRatingsAverage: ${meal.ingredient_reviews}")
                        supabase.from("Meals_duplicate2").update(
                            {
                                set("ingredient_reviews", meal.ingredient_reviews)
                                set("review_num",++meal.review_num)
                            }
                        ) {
                            filter {
                                eq("id", mealId)
                            }
                        }.decodeSingle<Meals>()
                    }
                } catch (e: Exception) {
                    e("TAG", "updateRatingsAverage: couldn't update data", e)

                }

            }

        }
        executorService.execute(futureTask)
        try {
            futureTask.get()
        } catch (e: Exception) {
            e("TAG", "Error waiting for future task", e)
        } finally {
            executorService.shutdown()
        }
    }

}
