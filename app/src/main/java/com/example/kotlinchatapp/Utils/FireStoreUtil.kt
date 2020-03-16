package com.example.kotlinchatapp.Utils

import android.content.Context
import com.example.kotlinchatapp.Models.*
import com.example.kotlinchatapp.RecyclerView.MessageItem
import com.example.kotlinchatapp.RecyclerView.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item


//object mean singleton
object FireStoreUtil {
    //lazy mean only when we needed
    private val fireStoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDecRef: DocumentReference
        get() = fireStoreInstance.document(
            "users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw java.lang.NullPointerException("user is null ")}"
        )

    private val chatChannelCollectionRef = fireStoreInstance.collection("chatChannels")

    fun initUserForFirstTime(onComplete: () -> Unit) {
        currentUserDecRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser =
                    User(FirebaseAuth.getInstance().currentUser?.displayName ?: "", "", null)
                currentUserDecRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }

    fun updateCurrentUser(name: String = " ", bio: String = "", profilePic: String? = "") {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePic != null) userFieldMap["profilePicPath"] = profilePic
        currentUserDecRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDecRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    fun addUserListener(context: Context, onlisten: (List<Item>) -> Unit): ListenerRegistration {
        return fireStoreInstance.collection("users")
            .addSnapshotListener { querySnap, exception ->
                if (exception != null)
                    return@addSnapshotListener
                val items = mutableListOf<Item>()
                querySnap?.documents?.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid) {
                        items.add(Person(it.toObject(User::class.java)!!, it.id, context))
                    }
                }
                onlisten(items)
            }
    }

    fun removeListner(regist: ListenerRegistration) = regist.remove()

    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {
        currentUserDecRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                //already there is a chat
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                val newChannel = chatChannelCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDecRef.collection("engagedChatChannels").document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                fireStoreInstance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels").document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))
                onComplete(newChannel.id)
            }
    }

    fun addchatMessagingListner(
        channelId: String,
        context: Context,
        onListen: (List<Item>) -> Unit
    ): ListenerRegistration {

        return chatChannelCollectionRef.document(channelId).collection("messages").orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    if (it["type"] == MessageType.TEXT) {
                        items.add(MessageItem(it.toObject(TextMessage::class.java)!!, context))
                    } else {
                        //Todo for images
                    }
                    onListen(items)

                }
            }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelCollectionRef.document(channelId).collection("messages").add(message)
    }
}