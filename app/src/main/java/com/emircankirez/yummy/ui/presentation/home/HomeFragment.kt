package com.emircankirez.yummy.ui.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter
    private val navController: NavController by lazy { findNavController() }

    // dialogs
    private var errorDialog: ErrorDialog? = null
    private var loadingDialog: LoadingDialog? = null

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
        errorDialog = null
        loadingDialog = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCategoryRecyclerView()
        listen()
        observe()
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
            val randomMealResponse = viewModel.randomMealResponse.value
            if(randomMealResponse is Resource.Success)
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToMealDetailFragment(randomMealResponse.data[0].id))
        }
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.categoryResponse.collect{
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
                            hideLoadingDialog {
                                adapter.listDiffer.submitList(it.data)
                            }
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
                            showErrorDialog(it.message)
                        }
                        Resource.Loading -> {}
                        is Resource.Success -> {
                            Glide.with(binding.root).load(it.data[0].photoUrl).into(binding.ivRandomMealPhoto)
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