package com.example.restaurantapp

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class Meals(
        val id: Int,
        var review_num: Int,
        val meal_name: String,
        val meal_price: Float,
        val description: String,
        val meal_image: String,
        val ingredients: List<String>,
        val ingredient_reviews: MutableList<Float>,
        val overall_rating: Float,
//        val latest_review: MutableList<Float>,
        val user_comments: List<String>
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readFloat(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.createStringArrayList() ?: emptyList(),
                parcel.createFloatArray()?.toMutableList() ?: mutableListOf<Float>(),
                parcel.readFloat(),
//                parcel.createFloatArray()?.toMutableList() ?: mutableListOf<Float>(),
                parcel.createStringArrayList() ?: emptyList()
        )
        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(id)
                parcel.writeInt(review_num)
                parcel.writeString(meal_name)
                parcel.writeFloat(meal_price)
                parcel.writeString(description)
                parcel.writeString(meal_image)
                parcel.writeStringList(ingredients)
                parcel.writeFloatArray(ingredient_reviews.toFloatArray())
                parcel.writeFloat(overall_rating)
//                parcel.writeFloatArray(latest_review.toFloatArray())
                parcel.writeStringList(user_comments)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Meals> {
                override fun createFromParcel(parcel: Parcel): Meals {
                        return Meals(parcel)
                }

                override fun newArray(size: Int): Array<Meals?> {
                        return arrayOfNulls(size)
                }
        }
}
