package com.example.myrecipes.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipe(activity: RecipeEntity)

    @Update
    suspend fun updateRecipe(activity: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(activity: RecipeEntity)

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :name || '%' COLLATE NOCASE")
    suspend fun getRecipesByName(name: String): List<RecipeEntity>
}
