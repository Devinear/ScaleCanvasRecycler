package com.example.customcanvas

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PERMISSION_CODE_READ    = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    private fun initUI() {
        checkPermission()
    }

    private fun checkPermission() : Boolean{
        // 읽기
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE_READ)
            return false
        }
        startActivity(Intent(this, CanvasScrollActivity::class.java))
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE_READ -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    checkPermission()
                else // PackageManager.PERMISSION_DENIED
                    finish()
            }
            else -> {}
        }
    }
}


