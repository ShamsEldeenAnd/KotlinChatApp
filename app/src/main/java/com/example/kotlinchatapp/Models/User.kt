package com.example.kotlinchatapp.Models

data class User(val name: String, val bio: String, val profilePicPath: String?) {
    constructor() : this("", "", null)
}