package com.emircankirez.yummy.ui.presentation.categoryMeal

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
import com.emircankirez.yummy.databinding.FragmentCategoryMealBinding
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryMealFragment : Fragment() {
    private var _binding: FragmentCategoryMealBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryMealViewModel by viewModels()
    private lateinit var adapter: MealAdapter
    private val navController: NavController by lazy { findNavController() }

    // dialogs
    private var errorDialog: ErrorDialog? = null
    private var loadingDialog: LoadingDialog? = null

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
        errorDialog = null
        loadingDialog = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryName = CategoryMealFragmentArgs.fromBundle(requireArguments()).categoryName

        binding.tvCategoryName.text = categoryName
        initMealRecyclerView()
        viewModel.getCategoryMeals(categoryName)
        listen()
        observe()
    }

    private fun initMealRecyclerView(){
        adapter = MealAdapter{ mealId ->
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
                            hideLoadingDialog()
                            showErrorDialog(it.message)
                        }
                        Resource.Loading -> {
                            showLoadingDialog()
                        }
                        is Resource.Success -> {
                            hideLoadingDialog{
                                adapter.listDiffer.submitList(it.data)
                            }
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

    private fun showLoadingDialog() {
        if (loadingDialog == null)
            loadingDialog = LoadingDialog(requireContext())
        loadingDialog?.show()
    }

    private fun hideLoadingDialog(callBack: () -> Unit = {}) {
        if (loadingDialog != null) {
            loadingDialog?.dismiss()
        }
        callBack.invoke()
    }
}