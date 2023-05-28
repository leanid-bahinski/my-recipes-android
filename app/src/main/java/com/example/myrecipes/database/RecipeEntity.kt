package com.example.myrecipes.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val ingredients: String,
    val instructions: String,
    val time: String,
    val photo: ByteArray?
) : Parcelable {
    companion object {
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
            return byteArray?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecipeEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (category != other.category) return false
        if (ingredients != other.ingredients) return false
        if (instructions != other.instructions) return false
        if (time != other.time) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + instructions.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        return result
    }
}
