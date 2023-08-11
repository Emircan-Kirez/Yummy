package com.emircankirez.yummy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emircankirez.yummy.databinding.ItemCategoryBinding
import com.emircankirez.yummy.domain.model.Category

class CategoryAdapter (
    private val onClick: (categoryName: String) -> Unit
): RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    val listDiffer = AsyncListDiffer(this, diffUtil)

    inner class CategoryHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onClick.invoke(listDiffer.currentList[position].name)
            }
        }
        fun bind(category: Category){
            Glide.with(binding.root).load(category.photoUrl).into(binding.ivCategoryPhoto)
            binding.tvCategoryName.text = category.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryHolder(binding)
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bind(listDiffer.currentList[position])
    }
}