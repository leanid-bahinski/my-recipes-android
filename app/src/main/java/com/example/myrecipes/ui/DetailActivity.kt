package com.example.myrecipes.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myrecipes.R
import com.example.myrecipes.database.AppDatabase
import com.example.myrecipes.database.RecipeEntity
import com.example.myrecipes.database.RecipeEntity.Companion.byteArrayToBitmap
import com.example.myrecipes.databinding.ActivityDetailBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var recipe: RecipeEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipe = intent.getParcelableExtra<RecipeEntity>("recipe")!!
        binding.photoImageView.setImageBitmap(byteArrayToBitmap(recipe.photo) ?: defaultImage())
        binding.idTextView.text = recipe.id.toString()
        binding.nameTextView.text = recipe.name
        binding.categoryTextView.text = recipe.category
        binding.ingredientsTextView.text = recipe.ingredients
        binding.instructionsTextView.text = recipe.instructions
        binding.timeTextView.text = recipe.time
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_submenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                showConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { dialog, _ ->
                val recipeDao = AppDatabase.getDatabase(this).recipeDao()
                GlobalScope.launch {
                    recipeDao.deleteRecipe(recipe)
                }
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun defaultImage(): Bitmap {
        return BitmapFactory.decodeResource(this.resources, R.drawable.ic_activity)
    }
}