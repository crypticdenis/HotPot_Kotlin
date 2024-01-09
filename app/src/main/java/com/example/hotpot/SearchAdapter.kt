package com.example.hotpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotpot.R
import com.example.hotpot.RecipeInSearch
import com.example.hotpot.User

class SearchAdapter(private var dataset: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Define view types for different data items
    private val USER_TYPE = 1
    private val RECIPE_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            USER_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user, parent, false)
                UserViewHolder(view)
            }
            RECIPE_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recipe_in_search, parent, false)
                RecipeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                val user = dataset[position] as User
                holder.bind(user)
            }
            is RecipeViewHolder -> {
                val recipe = dataset[position] as RecipeInSearch
                holder.bind(recipe)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataset[position]) {
            is User -> USER_TYPE
            is RecipeInSearch -> RECIPE_TYPE
            else -> throw IllegalArgumentException("Invalid data type")
        }
    }

    // Update the dataset with new data and notify the adapter
    fun updateData(newDataset: List<Any>) {
        dataset = newDataset
        notifyDataSetChanged()
    }

    // ViewHolder for User items
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)

        fun bind(user: User) {
            userNameTextView.text = user.userName
            // Add any other binding logic for User items
        }
    }

    // ViewHolder for RecipeInSearch items
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)

        fun bind(recipe: RecipeInSearch) {
            recipeNameTextView.text = recipe.recipeName
            // Add any other binding logic for RecipeInSearch items
        }
    }
}
