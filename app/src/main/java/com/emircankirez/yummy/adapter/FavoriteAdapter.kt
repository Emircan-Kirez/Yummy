package com.emircankirez.yummy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emircankirez.yummy.databinding.ItemFavoriteBinding
import com.emircankirez.yummy.domain.model.Meal

class FavoriteAdapter (
    private val onClick: (mealId : String) -> Unit
): RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }
    }

    val listDiffer = AsyncListDiffer(this, diffUtil)
    inner class FavoriteHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION)
                    onClick.invoke(listDiffer.currentList[position].id)
            }
        }

        fun bind(meal: Meal){
            Glide.with(binding.root).load(meal.photoUrl).into(binding.ivMealPhoto)
            binding.tvMealName.text = meal.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteHolder(binding)
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun onBindViewHolder(holder: FavoriteHolder, position: Int) {
        holder.bind(listDiffer.currentList[position])
    }
}