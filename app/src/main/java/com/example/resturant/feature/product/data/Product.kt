package com.example.resturant.feature.product.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONObject

enum class ProductCategory(val label: String) {
    BAR("Bar"),
    PIZZA("Pizza"),
    PASTA("Pasta"),
    SNACK("Snack"),
    BURGER("Burger"),
    APPETIZER("Appetizer"),
    STEAK("Steak"),
    OTHER("Other")
}

data class Ingredient(
    val name: String,
    val unit: String,
    val baseQuantity: Double,
    val consumed: Double,
    val basePrice: Double
) {
    val cost: Double get() = if (baseQuantity > 0) consumed / baseQuantity * basePrice else 0.0
}

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: ProductCategory,
    val ingredients: List<Ingredient>,
    val totalCost: Double
)

class ProductConverters {
    @TypeConverter fun fromCategory(c: ProductCategory): String = c.name
    @TypeConverter fun toCategory(v: String): ProductCategory = ProductCategory.valueOf(v)

    @TypeConverter
    fun fromIngredients(list: List<Ingredient>): String {
        val arr = JSONArray()
        list.forEach { ing ->
            val o = JSONObject()
            o.put("name", ing.name)
            o.put("unit", ing.unit)
            o.put("baseQuantity", ing.baseQuantity)
            o.put("consumed", ing.consumed)
            o.put("basePrice", ing.basePrice)
            arr.put(o)
        }
        return arr.toString()
    }

    @TypeConverter
    fun toIngredients(json: String): List<Ingredient> {
        if (json.isBlank()) return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<Ingredient>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Ingredient(
                    name = o.optString("name"),
                    unit = o.optString("unit"),
                    baseQuantity = o.optDouble("baseQuantity", 0.0),
                    consumed = o.optDouble("consumed", 0.0),
                    basePrice = o.optDouble("basePrice", 0.0)
                )
            )
        }
        return list
    }
}