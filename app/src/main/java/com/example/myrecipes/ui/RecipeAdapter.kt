package com.example.myrecipes.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myrecipes.R
import com.example.myrecipes.database.RecipeEntity
import com.example.myrecipes.database.RecipeEntity.Companion.byteArrayToBitmap

class RecipeAdapter(context: Context, val resource: Int, val items: List<RecipeEntity>) :
    ArrayAdapter<RecipeEntity>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val recipe = items[position]
        val imageView = view.findViewById<ImageView>(R.id.photo_image_view)
        val nameTextView = view.findViewById<TextView>(R.id.name_text_view)
        val categoryTextView = view.findViewById<TextView>(R.id.category_text_view)
        val timeTextView = view.findViewById<TextView>(R.id.time_text_view)

        imageView.setImageBitmap(byteArrayToBitmap(recipe.photo) ?: defaultImage())
        nameTextView.text = recipe.name
        categoryTextView.text = recipe.category
        timeTextView.text = recipe.time

        return view
    }

    private fun defaultImage(): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.ic_activity)
    }
}
