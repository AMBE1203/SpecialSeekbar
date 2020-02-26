package com.example.customseekbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        my_seek_bar.setListener(object : SpecialSeekBar.OnCustomsSeekbarChangeListener {
            override fun onChanged(
                seekbarVertical: SpecialSeekBar,
                progress: Int,
                frommUser: Boolean
            ) {

                if (frommUser){
                    Log.e("AAAAAAAA", "$progress")
                }

            }

            override fun onStartTrackingTouch(seekbarVertical: SpecialSeekBar) {
            }

            override fun onStopTrackingTouch(seekbarVertical: SpecialSeekBar) {
            }
        })
    }
}
