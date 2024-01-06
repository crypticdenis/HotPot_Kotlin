package com.example.hotpot;

import java.io.Serializable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

data class Recipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: String,
    val details: String,
    val tags: List<String>
): Serializable
{
    // Leerer Konstruktor für Firebase
    constructor() : this("", "", listOf(), "", "", listOf())
}

fun uploadRecipes(recipes: List<Recipe>) {
    val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Recipes")

    recipes.forEachIndexed { index, recipe ->
        val newRecipeReference = databaseReference.child(index.toString())
        newRecipeReference.child("name").setValue(recipe.name)
        newRecipeReference.child("description").setValue(recipe.description)
        newRecipeReference.child("ingredients").setValue(recipe.ingredients)
        newRecipeReference.child("instructions").setValue(recipe.instructions)
        newRecipeReference.child("details").setValue(recipe.details)
        newRecipeReference.child("tags").setValue(recipe.tags)
    }
}
