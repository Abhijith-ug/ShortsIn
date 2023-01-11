package com.example.mymvvmnewsapp.util

import android.content.Context
import com.example.mymvvmnewsapp.models.Country
import com.example.mymvvmnewsapp.models.NewsCategory
import com.example.mynewsappdemo.R


class Constants {
    companion object {
        const val IS_DEBUG = false

        // Debug
        lateinit var debugContext: Context

        const val API_KEY = "f1b60e7c9da94c4e941f3ed0d4d927f1"
        const val BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 500L
        const val QUERY_PAGE_SIZE = 20

        const val CONTACT_US = "https://abhijith-ug.github.io/ShortIn-Contact-Page/"

        // Remember login credentials.
        const val USER_AUTH = "user_auth"
        const val SHARED_KEY_EMAIL = "email"
        const val SHARED_KEY_PASSWORD = "password"
        const val SHARED_KEY_IS_LOGGED_IN = "is_logged_in"

        val NEWS_CATEGORIES = mutableListOf(
            NewsCategory(1, "Business", "business"),
            NewsCategory(2, "Entertainment", "entertainment"),
            NewsCategory(3, "General", "general"),
            NewsCategory(4, "Health", "health"),
            NewsCategory(5, "Science", "science"),
            NewsCategory(6, "Sports", "sports"),
            NewsCategory(7, "Technology", "technology"),
        )

        val COUNTRIES = arrayListOf<Country>(
            Country(1,"India","in",R.drawable.`in`),
            Country(2, "Argentina", "ar", R.drawable.argentina),
            Country(3, "Austria", "at", R.drawable.austria),
            Country(4, "United States of America", "us", R.drawable.usa),
            Country(5, "United Arab Emirates", "ae", R.drawable.uae),


        )


        // Firebase values.
        const val PROFILE_IMAGES = "profileImages"
        const val USERS = "users"
        const val FULL_NAME = "fullName"
        const val EMAIL = "email"
        const val COUNTRY_CODE = "countryCode"
        const val COUNTRY = "country"
        const val ACTIVE_STATUS = "activeStatus"
        const val USER_ROLE = "userRole"
        const val UID = "uid"
        const val IS_USER_DEFAULT_VALUE = true
        const val USER_DEFAULT_COUNTRY_VALUE = "us"

        // Activity bundles
        const val IS_NEW_SUBSCRIPTION_TO_EXCLUSIVE_NEWS = "isNewSubscriptionToExclusiveNews"

        // Fragment tags
        const val EXCLUSIVE_NEWS_FRAGMENT_TAG = "exclusiveNewsFragment"


        const val defaultUserRole = true
    }
}