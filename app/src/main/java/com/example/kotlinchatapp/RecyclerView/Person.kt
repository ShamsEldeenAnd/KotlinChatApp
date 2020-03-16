package com.example.kotlinchatapp.RecyclerView

import android.content.Context
import com.example.kotlinchatapp.Glide.GlideApp
import com.example.kotlinchatapp.Models.User
import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.Utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.person_item_layout.*


class Person(val person: User, val useid: String, private val context: Context) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
        if (person.profilePicPath!=null){
            GlideApp.with(context).load(StorageUtil.pathToRefrence(person.profilePicPath))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.imageView_profile_picture)
        }

    }

    override fun getLayout() = R.layout.person_item_layout
}