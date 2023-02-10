package com.lex.placesdiary.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lex.placesdiary.R
import com.lex.placesdiary.databinding.ActivityPlacesDiaryDetailsBinding
import com.lex.placesdiary.models.PlacesDiaryModel

class PlacesDiaryDetailsActivity : AppCompatActivity() {

    private var binding : ActivityPlacesDiaryDetailsBinding? = null

    private var placesDiaryDetailsModel : PlacesDiaryModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesDiaryDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            placesDiaryDetailsModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as PlacesDiaryModel
        }

        if (placesDiaryDetailsModel != null){
            binding?.ivDetailsImage?.setImageURI(Uri.parse(placesDiaryDetailsModel!!.image))
            binding?.tvDetailsDescription?.text = placesDiaryDetailsModel!!.description
            binding?.tvDetailsLocation?.text = placesDiaryDetailsModel!!.location
            binding?.tvDetailsDate?.text = placesDiaryDetailsModel!!.date
        }

        setSupportActionBar(binding?.toolbarPlaceDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = placesDiaryDetailsModel?.title
        binding?.toolbarPlaceDetails?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}