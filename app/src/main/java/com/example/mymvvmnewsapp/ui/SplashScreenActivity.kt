package com.example.mymvvmnewsapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.mymvvmnewsapp.models.User
import com.example.mymvvmnewsapp.repository.UserRepository
import com.example.mymvvmnewsapp.util.Constants.Companion.IS_USER_DEFAULT_VALUE
import com.example.mymvvmnewsapp.util.Constants.Companion.USER_DEFAULT_COUNTRY_VALUE
import com.example.mymvvmnewsapp.util.UiState
import com.example.mynewsappdemo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        com.example.mymvvmnewsapp.Session.user = User("", "", "", "us", IS_USER_DEFAULT_VALUE)
        val auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            //Open the next activity.
            val intent: Intent
            if (auth.currentUser == null) {
                startActivity(Intent(this@SplashScreenActivity, UserActivity::class.java))
                finish()
            } else {
                getUserDataToSessionAndGotoNews(auth.uid.toString())
            }
        }, 1000)
    }

    private fun getUserDataToSessionAndGotoNews(uid: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val repository = UserRepository(application)
            repository.getUserFromFireStore(uid) {
                when (it) {
                    is UiState.Failure -> {
                        com.example.mymvvmnewsapp.Session.user = User("", "", "", USER_DEFAULT_COUNTRY_VALUE, IS_USER_DEFAULT_VALUE)
                        startActivity(
                            Intent(this@SplashScreenActivity, UserActivity::class.java)
                        )
                        finish()
                    }
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        com.example.mymvvmnewsapp.Session.user = it.data
                        startActivity(
                            Intent(this@SplashScreenActivity, NewsActivity::class.java)
                        )
                        finish()
                    }
                }
            }
        }
    }
}