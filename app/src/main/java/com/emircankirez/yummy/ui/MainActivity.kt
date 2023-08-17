package com.emircankirez.yummy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.emircankirez.yummy.R
import com.emircankirez.yummy.common.extensions.hide
import com.emircankirez.yummy.common.extensions.show
import com.emircankirez.yummy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val bottomNavigationView: BottomNavigationView by lazy { findViewById(R.id.bottomNavigationView) }
    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment }
    private val navController: NavController by lazy { navHostFragment.findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpBottomNavigationView(bottomNavigationView, navController)
    }

    private fun setUpBottomNavigationView(
        bottomNavigationView: BottomNavigationView,
        navController: NavController
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    bottomNavigationView.show()
                }

                R.id.searchFragment -> {
                    bottomNavigationView.show()
                }

                R.id.userRecipeFragment -> {
                    bottomNavigationView.show()
                }

                R.id.favoriteFragment -> {
                    bottomNavigationView.show()
                }

                else -> {
                    bottomNavigationView.hide()
                }
            }
            bottomNavigationView.setupWithNavController(navController)
            // aynı item'a tıklayınca sayfa yenilenmesin
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                if(item.itemId != bottomNavigationView.selectedItemId){
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
                true
            }
        }
    }
}