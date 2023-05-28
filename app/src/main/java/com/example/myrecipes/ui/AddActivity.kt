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
import com.example.myrecipes.database.RecipeEntity
import com.example.myrecipes.database.RecipeEntity.Companion.bitmapToByteArray
import com.example.myrecipes.databinding.ActivityAddBinding
import com.example.myrecipes.repository.RecipeRepository
import com.example.myrecipes.viewmodel.RecipeViewModel
import com.example.myrecipes.viewmodel.RecipeViewModelFactory
import java.io.IOException

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var recipeViewModel: RecipeViewModel
    private var selectedPhoto: Bitmap? = null

    companion object {
        private const val REQUEST_IMAGE_PICKER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.photoImageView.setImageBitmap(defaultImage())
        binding.btnSelectImageView.setOnClickListener { openImagePicker() }
        binding.addButton.setOnClickListener { addActivity() }

        val recipeRepository = RecipeRepository(AppDatabase.getDatabase(this).recipeDao())
        val recipeViewModelFactory = RecipeViewModelFactory(recipeRepository)
        recipeViewModel = ViewModelProvider(this, recipeViewModelFactory).get(RecipeViewModel::class.java)
    }

    private fun addActivity() {

        val recipeEntity = RecipeEntity(
            name = binding.nameEditText.text.toString(),
            category = binding.categorySpinner.selectedItem.toString(),
            ingredients = binding.ingredientsEditText.text.toString(),
            instructions = binding.instructionsEditText.text.toString(),
            time = binding.timeEditText.text.toString(),
            photo = bitmapToByteArray(selectedPhoto))

        recipeViewModel.insertRecipe(recipeEntity)

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
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