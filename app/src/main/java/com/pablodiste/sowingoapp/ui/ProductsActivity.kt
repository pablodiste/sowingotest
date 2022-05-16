package com.pablodiste.sowingoapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pablodiste.sowingoapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.products)
        setContentView(R.layout.activity_products)
    }
}