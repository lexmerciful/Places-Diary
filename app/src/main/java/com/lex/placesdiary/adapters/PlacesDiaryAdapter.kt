package com.lex.placesdiary.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lex.placesdiary.databinding.PlacesDiaryRvItemBinding
import com.lex.placesdiary.models.PlacesDiaryModel

class PlacesDiaryAdapter(private val context: Context,
                         private val placesDiaryList: ArrayList<PlacesDiaryModel>)
    :RecyclerView.Adapter<PlacesDiaryAdapter.ViewHolder>() {

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
        }
    }

    override fun getItemCount(): Int {
        return placesDiaryList.size
    }

}