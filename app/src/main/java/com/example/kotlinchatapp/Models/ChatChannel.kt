package com.example.kotlinchatapp.Models


//it's a documents
data class ChatChannel(val userids: MutableList<String>) {

    //adding cons for firebase
    constructor() : this(mutableListOf())
}