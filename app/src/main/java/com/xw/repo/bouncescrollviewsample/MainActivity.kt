package com.xw.repo.bouncescrollviewsample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.transparentStatusBar(this)

        setContentView(R.layout.activity_main)

        val lp = mToolbar.layoutParams as FrameLayout.LayoutParams
        lp.topMargin = Util.getStatusBarHeight()
        mToolbar.layoutParams = lp
    }

    fun onClick(v: View) {
        val intent = Intent()
        when {
            v.id == R.id.mHouseStarkTV ->
                intent.setClass(this, HouseStarkActivity::class.java)
            v.id == R.id.mHouseTargaryenTV ->
                intent.setClass(this, HouseTargaryenActivity::class.java)
            v.id == R.id.mHouseLannisterTV ->
                intent.setClass(this, HouseLannisterActivity::class.java)
            else ->
                intent.setClass(this, HouseBaratheonActivity::class.java)
        }
        startActivity(intent)
    }

}
