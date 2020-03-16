package com.example.kotlinchatapp.RecyclerView

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.example.kotlinchatapp.Models.Message
import com.example.kotlinchatapp.Models.TextMessage
import com.example.kotlinchatapp.R
import com.google.firebase.auth.FirebaseAuth

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.message_item_layout.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import java.text.SimpleDateFormat

 class MessageItem(private val message: TextMessage , private val context: Context)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView_message_text.text  = message.text
        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)
    }
    override fun getLayout() = R.layout.message_item_layout

    private fun setTimeText(viewHolder: ViewHolder) {
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_white
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = lParams
            }
        }
        else {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_primary_color
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = lParams
            }
        }
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is MessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

     override fun equals(other: Any?): Boolean {
         return isSameAs(other as MessageItem)
     }
 }