package com.emircankirez.yummy.ui.presentation.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.emircankirez.yummy.adapter.MealAdapter
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.databinding.FragmentSearchBinding
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MealAdapter
    private val navController: NavController by lazy { findNavController() }

    // dialogs
    private var errorDialog: ErrorDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        errorDialog = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMealRecyclerView()
        listen()
        observe()
    }

    private fun initMealRecyclerView(){
        adapter = MealAdapter { mealId ->
            navController.navigate(SearchFragmentDirections.actionSearchFragmentToMealDetailFragment(mealId))
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
                viewModel.getMealsByName(newText.orEmpty())
                return true
            }
        })
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.searchResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            showErrorDialog(it.message)
                        }
                        Resource.Loading -> {}
                        is Resource.Success -> {
                            val data = it.data
                            adapter.listDiffer.submitList(data)
                        }
                    }
                }
            }
        }
    }

    private fun showErrorDialog(desc: String, callBack: () -> Unit = {}){
        if(errorDialog == null)
            errorDialog = ErrorDialog(requireContext())

        errorDialog?.show(desc, callBack)
    }
}