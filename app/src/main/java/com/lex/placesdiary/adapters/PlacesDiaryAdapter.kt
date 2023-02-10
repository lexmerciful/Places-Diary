package com.lex.placesdiary.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lex.placesdiary.activities.AddPlaceDiaryActivity
import com.lex.placesdiary.activities.MainActivity
import com.lex.placesdiary.database.DatabaseHelper
import com.lex.placesdiary.databinding.PlacesDiaryRvItemBinding
import com.lex.placesdiary.models.PlacesDiaryModel

class PlacesDiaryAdapter(private val context: Context,
                         private val placesDiaryList: ArrayList<PlacesDiaryModel>)
    :RecyclerView.Adapter<PlacesDiaryAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class ViewHolder(private val binding: PlacesDiaryRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(model: PlacesDiaryModel){
            binding.ivImageItem.setImageURI(Uri.parse(model.image))
            binding.tvTitleItem.text = model.title
            binding.tvDescriptionItem.text = model.description
            binding.tvDateItem.text = model.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlacesDiaryRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = placesDiaryList[position]

        if (holder is ViewHolder){
            holder.bindItem(model)

            holder.itemView.setOnClickListener{
                if (onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    fun NotifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddPlaceDiaryActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, placesDiaryList[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun removeAt(position: Int){
        val dbHelper = DatabaseHelper(context)
        val isDeleted = dbHelper.deletePlaceDiary(placesDiaryList[position])
        if (isDeleted > 0){
            placesDiaryList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: PlacesDiaryModel)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return placesDiaryList.size
    }

}