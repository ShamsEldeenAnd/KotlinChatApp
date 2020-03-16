package com.example.kotlinchatapp.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinchatapp.ChatActivity
import com.example.kotlinchatapp.Constants.AppConstants

import com.example.kotlinchatapp.R
import com.example.kotlinchatapp.RecyclerView.Person
import com.example.kotlinchatapp.Utils.FireStoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.support.v4.startActivity


class PeopleFragment : Fragment() {


    //if there is new user register it's listen to that user
    private lateinit var userListnerRegistration: ListenerRegistration
    private var shouldInitPersons = true
    private lateinit var peopleSection: Section
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userListnerRegistration = FireStoreUtil.addUserListener(this.activity!!, this::UpdateList)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FireStoreUtil.removeListner(userListnerRegistration)
        shouldInitPersons = true
    }

    private fun UpdateList(items: List<Item>) {
        fun init (){
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter  = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitPersons = false
        }

        fun UpdateItems(){
            peopleSection.update(items)
        }

        if (shouldInitPersons)
            init()
        else
            UpdateItems()
    }

    private  val onItemClick = OnItemClickListener{item , view ->
        if (item is Person){
            startActivity<ChatActivity>(
                AppConstants.USER_NAME to item.person.name ,
                AppConstants.USER_ID to item.useid

            )
        }
    }


}
