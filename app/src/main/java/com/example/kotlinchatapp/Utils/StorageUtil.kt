package com.example.kotlinchatapp.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.coroutines.experimental.asReference
import java.lang.NullPointerException
import java.util.*

object StorageUtil {
    private  val storageInstant: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val getCurrentUserRef : StorageReference
            get() = storageInstant.reference
                .child(FirebaseAuth.getInstance().currentUser?.uid?:throw NullPointerException("UID is null"))

    fun uploadProfilePhoto(imageByte : ByteArray , onSucess: (imagePath:String)->Unit){
            val ref =getCurrentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageByte)}")
                ref.putBytes(imageByte)
                    .addOnSuccessListener {
                        onSucess(ref.path)
                    }
    }


    fun pathToRefrence(path:String)= storageInstant.getReference(path)
}