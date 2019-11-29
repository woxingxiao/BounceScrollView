package com.xw.repo.bouncescrollviewsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_house_lannister.*
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 *
 * <p>
 * Created by McQueen on 2018-06-04.
 */
class HouseLannisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_lannister)

        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        mBubbleSeekBar.setProgress(mBounceScrollView.bounceDelay.toFloat())
        mBubbleSeekBar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                mBounceScrollView.bounceDelay = progress.toLong()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}