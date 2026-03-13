package com.buidlstack.stacksubil.grfed

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication

class BuildStackGlobalLayoutUtil {

    private var buildStackMChildOfContent: View? = null
    private var buildStackUsableHeightPrevious = 0

    fun buildStackAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        buildStackMChildOfContent = content.getChildAt(0)

        buildStackMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val buildStackUsableHeightNow = buildStackComputeUsableHeight()
        if (buildStackUsableHeightNow != buildStackUsableHeightPrevious) {
            val buildStackUsableHeightSansKeyboard = buildStackMChildOfContent?.rootView?.height ?: 0
            val buildStackHeightDifference = buildStackUsableHeightSansKeyboard - buildStackUsableHeightNow

            if (buildStackHeightDifference > (buildStackUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(BuildStackApplication.buildStackInputMode)
            } else {
                activity.window.setSoftInputMode(BuildStackApplication.buildStackInputMode)
            }
//            mChildOfContent?.requestLayout()
            buildStackUsableHeightPrevious = buildStackUsableHeightNow
        }
    }

    private fun buildStackComputeUsableHeight(): Int {
        val r = Rect()
        buildStackMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}