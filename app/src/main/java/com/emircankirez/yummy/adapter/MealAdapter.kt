package com.emircankirez.yummy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emircankirez.yummy.databinding.ItemCategoryMealBinding
import com.emircankirez.yummy.domain.model.CategoryMeal

class MealAdapter(
    private val onClick: (mealId: String) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<CategoryMeal>() {
        override fun areItemsTheSame(oldItem: CategoryMeal, newItem: CategoryMeal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryMeal, newItem: CategoryMeal): Boolean {
            return oldItem == newItem
        }
    }

    val listDiffer = AsyncListDiffer(this, diffUtil)

    inner class MealHolder(private val binding: ItemCategoryMealBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION)
                    onClick.invoke(listDiffer.currentList[position].id)
            }
        }
        fun bind(categoryMeal: CategoryMeal){
            Glide.with(binding.root).load(categoryMeal.photoUrl).into(binding.ivMealPhoto)
            binding.tvMealName.text = categoryMeal.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealHolder {
        val binding = ItemCategoryMealBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealHolder(binding)
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun onBindViewHolder(holder: MealHolder, position: Int) {
        holder.bind(listDiffer.currentList[position])
    }
}