package com.buidlstack.stacksubil.grfed.data.utils

import android.util.Log
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class BuildStackPushToken {

    suspend fun buildStackGetToken(
        buildStackMaxAttempts: Int = 3,
        buildStackDelayMs: Long = 1500
    ): String {

        repeat(buildStackMaxAttempts - 1) {
            try {
                val buildStackToken = FirebaseMessaging.getInstance().token.await()
                return buildStackToken
            } catch (e: Exception) {
                Log.e(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(buildStackDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}