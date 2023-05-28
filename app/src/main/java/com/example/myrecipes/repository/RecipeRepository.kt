package com.example.myrecipes.repository

import androidx.lifecycle.LiveData
import com.example.myrecipes.database.RecipeDao
import com.example.myrecipes.database.RecipeEntity

class RecipeRepository(private val recipeDao: RecipeDao) {

    suspend fun insertRecipe(recipe: RecipeEntity) {
        recipeDao.insertRecipe(recipe)
    }

    suspend fun updateRecipe(recipe: RecipeEntity) {
        recipeDao.updateRecipe(recipe)
    }

    suspend fun deleteRecipe(recipe: RecipeEntity) {
        recipeDao.deleteRecipe(recipe)
    }

    fun getAllRecipes(): LiveData<List<RecipeEntity>> {
        return recipeDao.getAllRecipes()
    }

    suspend fun getRecipesByName(name: String): List<RecipeEntity> {
        return recipeDao.getRecipesByName(name)
    }
}
