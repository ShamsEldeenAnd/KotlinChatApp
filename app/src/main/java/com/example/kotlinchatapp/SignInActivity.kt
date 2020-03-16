package com.example.kotlinchatapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.whenCreated
import com.example.kotlinchatapp.Utils.FireStoreUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class SignInActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1
    private val SignInProviders = listOf(
        AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).setRequireName(true).build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInBtn.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(SignInProviders)
                .setLogo(R.drawable.ic_login_logo).build()

            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val progressDialog =
                    indeterminateProgressDialog(message = "Setting Up your Account ")
                FireStoreUtil.initUserForFirstTime {

                    //this is onSucess fun
                    //just to avoid re sign in
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                    progressDialog.dismiss()
                }

            }else if (resultCode == Activity.RESULT_CANCELED){
                if (response == null) return

                when(response.error?.errorCode){
                    ErrorCodes.NO_NETWORK -> longSnackbar(rellaout , "No Network")
                    ErrorCodes.UNKNOWN_ERROR-> longSnackbar(rellaout , "Unknown Error")

                }
            }
        }
    }
}
