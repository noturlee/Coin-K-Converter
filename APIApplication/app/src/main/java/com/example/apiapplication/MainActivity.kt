package com.example.apiapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var BtnNextPage: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BtnNextPage = findViewById(R.id.NextPage)

        BtnNextPage.setOnClickListener {
            val intent = Intent(this, AppActivity::class.java)
            startActivity(intent)
        }

    }
}