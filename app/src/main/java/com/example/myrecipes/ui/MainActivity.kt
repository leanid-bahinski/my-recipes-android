package com.example.myrecipes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myrecipes.R
import com.example.myrecipes.database.AppDatabase
import com.example.myrecipes.database.RecipeEntity
import com.example.myrecipes.databinding.ActivityMainBinding
import com.example.myrecipes.repository.RecipeRepository
import com.example.myrecipes.viewmodel.RecipeViewModel
import com.example.myrecipes.viewmodel.RecipeViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeViewModel: RecipeViewModel

    private val recipes = mutableListOf<RecipeEntity>()
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipeRepository = RecipeRepository(AppDatabase.getDatabase(this).recipeDao())
        val recipeViewModelFactory = RecipeViewModelFactory(recipeRepository)
        recipeViewModel = ViewModelProvider(this, recipeViewModelFactory).get(RecipeViewModel::class.java)

        recipeAdapter = RecipeAdapter(this, R.layout.activity_list_item, recipes)
        binding.listView.adapter = recipeAdapter

        registerForContextMenu(binding.listView)

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("recipe", recipes[position])
            startActivity(intent)
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        recipeViewModel.allRecipes.observe(this, Observer { recipeList ->
            filterRecipes()
        })
    }

    override fun onResume() {
        super.onResume()
        recipeViewModel.getAllRecipes()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText.orEmpty()
                filterRecipes()
                return true
            }
        })

        return true
    }

    private fun filterRecipes() {
        val recipeList = recipeViewModel.allRecipes.value
        recipeList?.let {
            recipes.clear()
            recipes.addAll(it.filter { recipeEntity ->
                recipeEntity.name.contains(searchQuery, ignoreCase = true)
            })
            recipeAdapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                recipes.sortBy { it.name }
                recipeAdapter.notifyDataSetChanged()
                true
            }
            R.id.action_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position
        val recipe = recipes[position]

        return when (item.itemId) {
            R.id.action_detail -> {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("recipe", recipes[position])
                startActivity(intent)
                true
            }
            R.id.action_edit -> {
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog(recipe)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog(recipe: RecipeEntity) {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { dialog, _ ->
                GlobalScope.launch {
                    recipeViewModel.deleteRecipe(recipe)
                }
                dialog.dismiss()
            }
            .setNegativeButton("No", null)
            .show()
    }
}