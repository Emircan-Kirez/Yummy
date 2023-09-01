package com.emircankirez.yummy.ui.presentation.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emircankirez.yummy.R
import com.emircankirez.yummy.adapter.FavoriteAdapter
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.databinding.FragmentFavoriteBinding
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.ui.LoginActivity
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: FavoriteAdapter

    @Inject
    lateinit var myPreferences: MyPreferences

    @Inject
    lateinit var resourceProvider: ResourceProvider

    // dialogs
    private var errorDialog: ErrorDialog? = null
    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.tbFavorite)
        binding.tbFavorite.title = ""
        setHasOptionsMenu(true)
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

        initFavoriteRecyclerView()
        addItemTouchHelper()
        showLoadingDialog()
        observe()
    }

    private fun initFavoriteRecyclerView(){
        adapter = FavoriteAdapter { mealId ->
            findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToMealDetailFragment(mealId))
        }
        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFavorite.adapter = adapter
    }

    private fun addItemTouchHelper(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteFavorite(adapter.listDiffer.currentList[viewHolder.adapterPosition])
            }

        }).attachToRecyclerView(binding.rvFavorite)
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.userResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            showErrorDialog(it.message)
                        }
                        Resource.Loading -> {}
                        is Resource.Success -> {
                            updateUI(it.data)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.favoritesResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            hideLoadingDialog()
                            showErrorDialog(it.message)
                        }
                        Resource.Loading -> {}
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
                viewModel.deleteResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            showErrorDialog(it.message)
                        }
                        Resource.Loading -> {}
                        is Resource.Success -> {
                            val meal = it.data
                            Snackbar.make(
                                binding.root,
                                resourceProvider.getString(R.string.favorite_meal_deleted),
                                Snackbar.LENGTH_LONG
                            ).setAction(resourceProvider.getString(R.string.undo)){
                                viewModel.addFavorite(meal)
                            }.show()
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(user: User){
        binding.apply {
            Glide.with(binding.root).load(user.photoUrl).into(binding.ivProfilePhoto)
            tvProfileName.text = getString(R.string.profile_name, user.name, user.surname)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_toolbar_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.btn_logout -> {
                Firebase.auth.signOut()
                myPreferences.isLogin = false
                val intentToLoginActivity = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intentToLoginActivity)
                requireActivity().finish()
                true
            }
            R.id.btn_edit_profile -> {
                val userResponse = viewModel.userResponse.value
                if(userResponse is Resource.Success){
                    val user = userResponse.data
                    findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToUserEditFragment(user))
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
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