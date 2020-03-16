package com.example.kotlinchatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kotlinchatapp.Fragments.MyAccountFragment
import com.example.kotlinchatapp.Fragments.PeopleFragment

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(PeopleFragment())
        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.fiends_fragment -> {
                    replaceFragment(PeopleFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.account_fragment-> {
                    replaceFragment(MyAccountFragment())
                    return@setOnNavigationItemSelectedListener true
                }else -> return@setOnNavigationItemSelectedListener false

            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commit()
    }
}
