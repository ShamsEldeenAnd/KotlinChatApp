package com.example.kotlinchatapp.Fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.kotlinchatapp.Glide.GlideApp

import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.SignInActivity
import com.example.kotlinchatapp.Utils.FireStoreUtil
import com.example.kotlinchatapp.Utils.StorageUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class MyAccountFragment : Fragment() {

    //vars
    private val RC_SELECTED_IMAGE = 2
    private lateinit var selectedImagesBytes: ByteArray
    private var isChaned = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        view.apply {
            profileImage.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    RC_SELECTED_IMAGE
                )
            }

            saveBtn.setOnClickListener {
                if (::selectedImagesBytes.isInitialized) {
                    //update fire base storage
                    StorageUtil.uploadProfilePhoto(selectedImagesBytes) { imagePath ->
                        FireStoreUtil.updateCurrentUser(
                            nameTxt.text.toString(),
                            bioTxt.text.toString(),
                            imagePath
                        )
                   toast("Saved Succsessfully ")
                    }
                } else {
                    FireStoreUtil.updateCurrentUser(
                        nameTxt.text.toString(),
                        bioTxt.text.toString(),
                        null
                    )
                }
            }
            signOutBtn.setOnClickListener {
                AuthUI.getInstance().signOut(this@MyAccountFragment.context!!)
                    .addOnCompleteListener {
                        startActivity(intentFor<SignInActivity>().newTask().clearTask())
                    }
            }

        }
        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECTED_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImagesBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImagesBytes)
                .into(profileImage)


            isChaned = true
        }
    }


    override fun onStart() {
        super.onStart()
        FireStoreUtil.getCurrentUser { user ->
            if (this@MyAccountFragment.isVisible) {
                nameTxt.setText(user.name)
                bioTxt.setText(user.bio)
                if (!isChaned && user.profilePicPath != null) {
                    GlideApp.with(this).load(StorageUtil.pathToRefrence(user.profilePicPath))
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(profileImage)
                }
            }
        }
    }
}
