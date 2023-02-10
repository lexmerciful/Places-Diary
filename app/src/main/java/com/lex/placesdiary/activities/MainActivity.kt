package com.lex.placesdiary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lex.placesdiary.adapters.PlacesDiaryAdapter
import com.lex.placesdiary.database.DatabaseHelper
import com.lex.placesdiary.databinding.ActivityMainBinding
import com.lex.placesdiary.models.PlacesDiaryModel
import com.lex.placesdiary.utils.SwipeToDeleteCallback
import com.lex.placesdiary.utils.SwipeToEditCallback

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

        placesAdapter.setOnClickListener(object : PlacesDiaryAdapter.OnClickListener{
            override fun onClick(position: Int, model: PlacesDiaryModel) {
                val intent = Intent(this@MainActivity, PlacesDiaryDetailsActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)

                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvPlaceDiaryList?.adapter as PlacesDiaryAdapter
                adapter.NotifyEditItem(this@MainActivity, viewHolder.absoluteAdapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvPlaceDiaryList)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvPlaceDiaryList?.adapter as PlacesDiaryAdapter
                adapter.removeAt(viewHolder.absoluteAdapterPosition)

                getPlacesDiaryListFromDB()
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding?.rvPlaceDiaryList)
    }

    override fun onResume() {
        super.onResume()
        getPlacesDiaryListFromDB()
    }

    companion object {
        var EXTRA_PLACE_DETAILS = "extra_place_details"
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
}