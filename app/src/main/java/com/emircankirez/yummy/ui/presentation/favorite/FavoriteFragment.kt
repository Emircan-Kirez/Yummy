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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.emircankirez.yummy.R
import com.emircankirez.yummy.adapter.FavoriteAdapter
import com.emircankirez.yummy.common.Resource
import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.databinding.FragmentFavoriteBinding
import com.emircankirez.yummy.domain.model.User
import com.emircankirez.yummy.ui.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
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
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFavoriteRecyclerView()
        observe()
    }

    private fun initFavoriteRecyclerView(){
        adapter = FavoriteAdapter { mealId ->
            findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToMealDetailFragment(mealId))
        }
        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvFavorite.adapter = adapter
    }

    private fun observe(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.userResponse.collect{
                    when(it){
                        Resource.Empty -> {}
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        Resource.Loading -> {
                            // loading alert dialog
                        }
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
}