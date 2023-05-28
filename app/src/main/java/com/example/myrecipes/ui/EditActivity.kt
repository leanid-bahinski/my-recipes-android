package com.example.myrecipes.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myrecipes.R
import com.example.myrecipes.database.AppDatabase
import com.example.myrecipes.database.RecipeDao
import com.example.myrecipes.database.RecipeEntity
import com.example.myrecipes.database.RecipeEntity.Companion.bitmapToByteArray
import com.example.myrecipes.database.RecipeEntity.Companion.byteArrayToBitmap
import com.example.myrecipes.databinding.ActivityEditBinding
import com.example.myrecipes.repository.RecipeRepository
import com.example.myrecipes.viewmodel.RecipeViewModel
import com.example.myrecipes.viewmodel.RecipeViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var recipeViewModel: RecipeViewModel
    private var selectedPhoto: Bitmap? = null

    companion object {
        private const val REQUEST_IMAGE_PICKER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recipeRepository = RecipeRepository(AppDatabase.getDatabase(this).recipeDao())
        val recipeViewModelFactory = RecipeViewModelFactory(recipeRepository)
        recipeViewModel = ViewModelProvider(this, recipeViewModelFactory).get(RecipeViewModel::class.java)

        val recipe = intent.getParcelableExtra<RecipeEntity>("recipe")!!
        binding.idEditText.setText(recipe.id.toString())
        binding.nameEditText.setText(recipe.name)
        binding.ingredientsEditText.setText(recipe.ingredients)
        binding.instructionsEditText.setText(recipe.instructions)
        binding.timeEditText.setText(recipe.time)
        selectedPhoto = byteArrayToBitmap(recipe.photo)
        binding.photoImageView.setImageBitmap(selectedPhoto ?: defaultImage())
        val categories = resources.getStringArray(R.array.recipe_category)
        val categoryIndex = categories.indexOf(recipe.category)
        binding.categorySpinner.setSelection(categoryIndex)

        binding.saveButton.setOnClickListener {

            val updatedRecipe = RecipeEntity(
                binding.idEditText.text.toString().toLong(),
                binding.nameEditText.text.toString(),
                binding.categorySpinner.selectedItem.toString(),
                binding.ingredientsEditText.text.toString(),
                binding.instructionsEditText.text.toString(),
                binding.timeEditText.text.toString(),
                bitmapToByteArray(selectedPhoto))

            recipeViewModel.updateRecipe(updatedRecipe)

            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.btnSelectImageView.setOnClickListener { openImagePicker() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditActivity.REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                try {
                    val inputStream = contentResolver.openInputStream(selectedImageUri)
                    selectedPhoto = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    binding.photoImageView.setImageBitmap(selectedPhoto)
                    binding.photoImageView.isVisible = true

                    Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun defaultImage(): Bitmap {
        return BitmapFactory.decodeResource(this.resources, R.drawable.ic_activity)
    }
}