package com.github.markushi.posenet

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.examples.posenet.PosenetFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)
        savedInstanceState ?: supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                PosenetFragment()
            )
            .commit()
    }
}
