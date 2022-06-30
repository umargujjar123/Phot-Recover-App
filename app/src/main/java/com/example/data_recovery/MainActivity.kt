package com.example.data_recovery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.data_recovery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.dataRecoveryBtn.setOnClickListener() {

            startActivity(Intent(this, CardsScreen::class.java))

        }
    }

}