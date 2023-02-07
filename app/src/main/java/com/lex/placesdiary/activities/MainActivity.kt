package com.lex.placesdiary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lex.placesdiary.adapters.PlacesDiaryAdapter
import com.lex.placesdiary.database.DatabaseHelper
import com.lex.placesdiary.databinding.ActivityMainBinding
import com.lex.placesdiary.models.PlacesDiaryModel

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this, AddPlaceDiaryActivity::class.java)
            startActivity(intent)
        }

        getPlacesDiaryListFromDB()
    }

    private fun getPlacesDiaryListFromDB(){
        val dbHelper = DatabaseHelper(this)
        val getPlacesDiaryList : ArrayList<PlacesDiaryModel> = dbHelper.getPlaceDiaryList()

        if (getPlacesDiaryList.size > 0){
            binding?.rvPlaceDiaryList?.visibility = View.VISIBLE
            binding?.tvNoPlacesRecord?.visibility = View.GONE

            setupPlacesDiaryRecyclerView(getPlacesDiaryList)
            }else{
            binding?.rvPlaceDiaryList?.visibility = View.GONE
            binding?.tvNoPlacesRecord?.visibility = View.VISIBLE
            }
        }

    private fun setupPlacesDiaryRecyclerView(placesDiaryList: ArrayList<PlacesDiaryModel>){
        binding?.rvPlaceDiaryList?.layoutManager = LinearLayoutManager(this)
        binding?.rvPlaceDiaryList?.setHasFixedSize(true)

        val placesAdapter = PlacesDiaryAdapter(this, placesDiaryList)
        binding?.rvPlaceDiaryList?.adapter = placesAdapter
    }

    override fun onResume() {
        super.onResume()
        getPlacesDiaryListFromDB()
    }
}