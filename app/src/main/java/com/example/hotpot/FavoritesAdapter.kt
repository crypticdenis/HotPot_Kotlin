package com.example.hotpot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hotpot.databinding.ItemFavoriteRecipeBinding
import com.example.hotpot.model.Recipe

class FavoritesAdapter(private var recipes: List<Recipe>) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    class FavoritesViewHolder(val binding: ItemFavoriteRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            // Bind data to the views
            binding.recipeNameTextView.text = recipe.name
            // Load image using Glide or Picasso into binding.recipeImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ItemFavoriteRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    fun updateData(newData: List<Recipe>) {
        recipes = newData
        notifyDataSetChanged()
    }

}
