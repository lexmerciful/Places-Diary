package com.lex.placesdiary.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lex.placesdiary.R
import com.lex.placesdiary.databinding.ActivityPlacesDiaryDetailsBinding

class PlacesDiaryDetailsActivity : AppCompatActivity() {

    private var binding : ActivityPlacesDiaryDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesDiaryDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarPlaceDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarPlaceDetails?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}