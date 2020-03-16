package com.example.kotlinchatapp.Models

import java.util.*

//mean singletone
object  MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    val time  : Date
    val senderId : String
    val type  : String

}