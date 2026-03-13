package com.buidlstack.stacksubil

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.buidlstack.stacksubil.grfed.BuildStackGlobalLayoutUtil
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import com.buidlstack.stacksubil.grfed.presentation.pushhandler.BuildStackPushHandler
import com.buidlstack.stacksubil.grfed.buildStackSetupSystemBars
import org.koin.android.ext.android.inject

class BuildStackActivity : AppCompatActivity() {

    private val buildStackPushHandler by inject<BuildStackPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildStackSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_build_stack)

        val buildStackRootView = findViewById<View>(android.R.id.content)
        BuildStackGlobalLayoutUtil().buildStackAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(buildStackRootView) { buildStackView, buildStackInsets ->
            val buildStackSystemBars = buildStackInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val buildStackDisplayCutout = buildStackInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val buildStackIme = buildStackInsets.getInsets(WindowInsetsCompat.Type.ime())


            val buildStackTopPadding = maxOf(buildStackSystemBars.top, buildStackDisplayCutout.top)
            val buildStackLeftPadding = maxOf(buildStackSystemBars.left, buildStackDisplayCutout.left)
            val buildStackRightPadding = maxOf(buildStackSystemBars.right, buildStackDisplayCutout.right)
            window.setSoftInputMode(BuildStackApplication.buildStackInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "ADJUST PUN")
                val buildStackBottomInset = maxOf(buildStackSystemBars.bottom, buildStackDisplayCutout.bottom)

                buildStackView.setPadding(buildStackLeftPadding, buildStackTopPadding, buildStackRightPadding, 0)

                buildStackView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = buildStackBottomInset
                }
            } else {
                Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "ADJUST RESIZE")

                val buildStackBottomInset = maxOf(buildStackSystemBars.bottom, buildStackDisplayCutout.bottom, buildStackIme.bottom)

                buildStackView.setPadding(buildStackLeftPadding, buildStackTopPadding, buildStackRightPadding, 0)

                buildStackView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = buildStackBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(BuildStackApplication.BUILD_STACK_MAIN_TAG, "Activity onCreate()")
        buildStackPushHandler.buildStackHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            buildStackSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        buildStackSetupSystemBars()
    }
}