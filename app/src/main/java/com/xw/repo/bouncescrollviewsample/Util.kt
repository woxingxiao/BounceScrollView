package com.xw.repo.bouncescrollviewsample

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * Created by woxingxiao on 2018-06-05.
 */
object Util {
    fun transparentStatusBar(activity: Activity) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    val statusBarHeight: Int
        get() {
            val resId = Resources.getSystem()
                .getIdentifier("status_bar_height", "dimen", "android")
            return Resources.getSystem().getDimensionPixelSize(resId)
        }
}