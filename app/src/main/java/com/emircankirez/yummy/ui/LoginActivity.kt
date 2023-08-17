package com.emircankirez.yummy.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.emircankirez.yummy.R
import com.emircankirez.yummy.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.login_nav_host) as NavHostFragment }
    private val navController: NavController by lazy { navHostFragment.findNavController() }
    private val w: Window by lazy { window }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.splashFragment -> {
                    hideStatusBar()
                }

                R.id.loginFragment -> {
                    showStatusBar()
                }

                R.id.forgotPasswordFragment -> {
                    showStatusBar()
                }

                R.id.registerFragment -> {
                    showStatusBar()
                }
            }
        }
    }

    private fun showStatusBar() {
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun hideStatusBar() {
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}