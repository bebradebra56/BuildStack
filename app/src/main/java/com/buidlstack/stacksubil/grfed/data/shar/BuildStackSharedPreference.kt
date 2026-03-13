package com.buidlstack.stacksubil.grfed.data.shar

import android.content.Context
import androidx.core.content.edit

class BuildStackSharedPreference(context: Context) {
    private val buildStackPrefs = context.getSharedPreferences("buildStackSharedPrefsAb", Context.MODE_PRIVATE)

    var buildStackSavedUrl: String
        get() = buildStackPrefs.getString(BUILD_STACK_SAVED_URL, "") ?: ""
        set(value) = buildStackPrefs.edit { putString(BUILD_STACK_SAVED_URL, value) }

    var buildStackExpired : Long
        get() = buildStackPrefs.getLong(BUILD_STACK_EXPIRED, 0L)
        set(value) = buildStackPrefs.edit { putLong(BUILD_STACK_EXPIRED, value) }

    var buildStackAppState: Int
        get() = buildStackPrefs.getInt(BUILD_STACK_APPLICATION_STATE, 0)
        set(value) = buildStackPrefs.edit { putInt(BUILD_STACK_APPLICATION_STATE, value) }

    var buildStackNotificationRequest: Long
        get() = buildStackPrefs.getLong(BUILD_STACK_NOTIFICAITON_REQUEST, 0L)
        set(value) = buildStackPrefs.edit { putLong(BUILD_STACK_NOTIFICAITON_REQUEST, value) }

    var buildStackNotificationState:Int
        get() = buildStackPrefs.getInt(BUILD_STACK_NOTIFICATION_STATE, 0)
        set(value) = buildStackPrefs.edit { putInt(BUILD_STACK_NOTIFICATION_STATE, value) }

    companion object {
        private const val BUILD_STACK_NOTIFICATION_STATE = "buildStackNotificationState"
        private const val BUILD_STACK_SAVED_URL = "buildStackSavedUrl"
        private const val BUILD_STACK_EXPIRED = "buildStackExpired"
        private const val BUILD_STACK_APPLICATION_STATE = "buildStackApplicationState"
        private const val BUILD_STACK_NOTIFICAITON_REQUEST = "buildStackNotificationRequest"
    }
}