package com.example.myrecipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipes.database.RecipeEntity
import com.example.myrecipes.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {

    val allRecipes: LiveData<List<RecipeEntity>> = recipeRepository.getAllRecipes()
    val searchQueryLiveData: MutableLiveData<String> = MutableLiveData()

    fun insertRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeRepository.insertRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeRepository.updateRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeRepository.deleteRecipe(recipe)
        }
    }

    fun getAllRecipes() {
        viewModelScope.launch {
            recipeRepository.getAllRecipes()
        }
    }

    fun getRecipesByName(name: String) {
        viewModelScope.launch {
            recipeRepository.getRecipesByName(name)
        }
    }
}
