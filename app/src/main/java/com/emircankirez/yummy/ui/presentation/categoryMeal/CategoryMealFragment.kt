package com.emircankirez.yummy.ui.presentation.categoryMeal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.emircankirez.yummy.adapter.CategoryMealAdapter
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.databinding.FragmentCategoryMealBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryMealFragment : Fragment() {
    private var _binding: FragmentCategoryMealBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryMealViewModel by viewModels()
    private lateinit var adapter: CategoryMealAdapter
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryName = CategoryMealFragmentArgs.fromBundle(requireArguments()).categoryName

        binding.tvCategoryName.text = categoryName
        viewModel.getCategoryMeals(categoryName)
        initCategoryMealAdapter()
        listen()
        observe()
    }

    private fun initCategoryMealAdapter(){
        adapter = CategoryMealAdapter{ mealId ->
            navController.navigate(CategoryMealFragmentDirections.actionCategoryMealFragmentToMealDetailFragment(mealId))
        }
        binding.rvMeal.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMeal.adapter = adapter
    }

    private fun listen(){
        binding.svMeal.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchQuery.value = newText.orEmpty()
                return true
            }
        })
    }

    private fun observe(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.filteredCategoryMealResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Resource.Loading -> {
                            // loading alert dialog
                        }
                        is Resource.Success -> {
                            adapter.listDiffer.submitList(it.data)
                            if (it.data.isEmpty()){
                                Toast.makeText(requireContext(), "Herhangi bir yemek bulunamadı.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}