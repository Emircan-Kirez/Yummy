package com.emircankirez.yummy.ui.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.emircankirez.yummy.adapter.CategoryAdapter
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.databinding.FragmentHomeBinding
import com.emircankirez.yummy.domain.model.Meal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter
    private val navController: NavController by lazy { findNavController() }
    private var randomMeal: Meal? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listen()
        observe()
        initCategoryRecyclerView()
    }

    private fun initCategoryRecyclerView(){
        adapter = CategoryAdapter{ categoryName ->
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToCategoryMealFragment(categoryName))
        }
        binding.rvCategory.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategory.adapter = adapter
    }

    private fun listen(){
        binding.cvRandomMeal.setOnClickListener {
            if(randomMeal != null)
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToMealDetailFragment(randomMeal!!.id))
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.categoryResponse.collect{
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
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.randomMealResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            randomMeal = null
                        }
                        Resource.Loading -> {
                            // loading alert dialog
                        }
                        is Resource.Success -> {
                            randomMeal = it.data[0]
                            Glide.with(binding.root).load(randomMeal!!.photoUrl).into(binding.ivRandomMealPhoto)
                        }
                    }
                }
            }
        }

    }

}