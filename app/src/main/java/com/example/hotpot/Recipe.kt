package com.example.hotpot.model;

import java.io.Serializable

data class Recipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: String,
    val details: String,
    val tags: List<String>
): Serializable
