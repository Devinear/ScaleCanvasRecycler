package com.example.customcanvas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CanvasScrollActivity : AppCompatActivity() {

    lateinit var canvasView : CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas_scoll)

        canvasView = findViewById(R.id.canvasView)
    }

}