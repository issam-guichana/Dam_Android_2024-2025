package com.example.gourmetia.remote

import android.content.Context
import android.content.SharedPreferences
import com.example.gourmetia.Screens.RecipeDetails
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

object SharedPrefsUtils {
    private const val PREFS_NAME = "recipe_preferences"
    private const val FAVORITE_RECIPES_KEY = "favorite_recipes"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveRecipe(context: Context, recipe: RecipeDetails) {
        val recipes = getFavoriteRecipes(context).toMutableList()
        recipes.add(recipe)

        val json = Json.encodeToString(recipes)
        getPrefs(context).edit().putString(FAVORITE_RECIPES_KEY, json).apply()
    }

    fun removeRecipe(context: Context, recipeId: String) {
        val recipes = getFavoriteRecipes(context).filter { it.id != recipeId }
        val json = Json.encodeToString(recipes)
        getPrefs(context).edit().putString(FAVORITE_RECIPES_KEY, json).apply()
    }

    fun getFavoriteRecipes(context: Context): List<RecipeDetails> {
        val json = getPrefs(context).getString(FAVORITE_RECIPES_KEY, "[]")
        return try {
            Json.decodeFromString(json!!)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearAllRecipes(context: Context) {
        getPrefs(context).edit().remove(FAVORITE_RECIPES_KEY).apply()
    }

    fun isRecipeFavorited(context: Context, recipeId: String): Boolean {
        return getFavoriteRecipes(context).any { it.id == recipeId }
    }
}