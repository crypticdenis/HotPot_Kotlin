package com.example.hotpot

data class UserProfile(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null, // URL to the user's profile picture
    val tags: List<String>? = null, // List of tags like "vegan", "keto", etc.
    val bio: String? = null, // Short bio, max 100 letters
    val friends: List<String>? = null // List of friend's UIDs
)
