package com.xw.repo.bouncescrollviewsample

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.transparentStatusBar(this)

        setContentView(R.layout.activity_main)

        val statusBarHeight = Util.getStatusBarHeight()
        mToolbar.setPadding(0, statusBarHeight, 0, 0)
        val lp = mToolbar.layoutParams as ViewGroup.MarginLayoutParams
        lp.height = statusBarHeight + (Resources.getSystem().displayMetrics.density * 56).toInt()
    }

    fun onClick(v: View) {
        val intent = Intent()
        when (v.id) {
            R.id.mHouseStarkTV ->
                intent.setClass(this, HouseStarkActivity::class.java)
            R.id.mHouseTargaryenTV ->
                intent.setClass(this, HouseTargaryenActivity::class.java)
            R.id.mHouseLannisterTV ->
                intent.setClass(this, HouseLannisterActivity::class.java)
            else -> intent.setClass(this, HouseBaratheonActivity::class.java)
        }
        startActivity(intent)
    }

}
