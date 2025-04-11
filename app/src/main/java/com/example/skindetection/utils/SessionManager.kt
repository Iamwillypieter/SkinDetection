package com.example.skindetection.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

object SessionManager {
    private const val PREF_NAME = "AppPrefs"
    private const val KEY_LAST_LOGIN_TIME = "lastLoginTime"
    private const val TIMEOUT_DURATION = 10 * 60 * 1000 // 10 menit dalam millis

    fun saveLoginTime(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(KEY_LAST_LOGIN_TIME, System.currentTimeMillis())
        editor.apply()
    }

    fun isSessionValid(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastLoginTime = sharedPref.getLong(KEY_LAST_LOGIN_TIME, 0)
        val currentTime = System.currentTimeMillis()

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val isWithinTimeout = (currentTime - lastLoginTime) < TIMEOUT_DURATION

        return isLoggedIn && isWithinTimeout
    }

    fun clearSession() {
        FirebaseAuth.getInstance().signOut()
    }
}