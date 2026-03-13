package com.buidlstack.stacksubil.grfed.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication

class BuildStackPushHandler {
    fun buildStackHandlePush(extras: Bundle?) {
        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map: MutableMap<String, String?> = HashMap()
            val ks = extras.keySet()
            val iterator: Iterator<String> = ks.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                map[key] = extras.getString(key)
            }
            Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Map from Push = $map")
            map.let {
                if (map.containsKey("url")) {
                    BuildStackApplication.BUILD_STACK_FB_LI = map["url"]
                    Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Push data no!")
        }
    }

}