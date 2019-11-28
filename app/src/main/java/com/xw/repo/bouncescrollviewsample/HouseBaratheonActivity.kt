package com.xw.repo.bouncescrollviewsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.daasuu.ei.Ease
import kotlinx.android.synthetic.main.activity_house_baratheon.*
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 *
 * <p>
 * Created by McQueen on 2018-06-04.
 */
class HouseBaratheonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_baratheon)

        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        interpolatorNameBtn.setOnClickListener {
            PickerFragment().show(supportFragmentManager, PickerFragment::class.java.simpleName)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    fun onInterpolatorSelected(ease: Ease) {
        interpolatorNameBtn.text = ease.name
        mBounceScrollView.setBounceInterpolator { input -> EasingProvider.get(ease, input) }
    }
}