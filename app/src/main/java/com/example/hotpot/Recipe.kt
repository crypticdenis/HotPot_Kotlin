package com.example.hotpot
import java.io.Serializable

data class Recipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: String,
    val details: String
): Serializable
