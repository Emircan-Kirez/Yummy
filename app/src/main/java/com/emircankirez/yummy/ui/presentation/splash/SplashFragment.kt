package com.emircankirez.yummy.ui.presentation.splash

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.emircankirez.yummy.R
import com.emircankirez.yummy.data.local.sharedPreferences.MyPreferences
import com.emircankirez.yummy.data.provider.ResourceProvider
import com.emircankirez.yummy.databinding.FragmentSplashBinding
import com.emircankirez.yummy.ui.MainActivity
import com.emircankirez.yummy.ui.presentation.dialog.ErrorDialog
import com.emircankirez.yummy.ui.presentation.dialog.InfoDialog
import com.emircankirez.yummy.ui.presentation.dialog.LoadingDialog
import com.emircankirez.yummy.ui.presentation.dialog.SuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var myPreferences: MyPreferences

    private var errorDialog: ErrorDialog? = null

    @Inject
    lateinit var resourceProvider: ResourceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        errorDialog = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        if (isNetworkAvailable()){
            Handler().postDelayed({
                if(myPreferences.isLogin){
                    val intentToMainActivity = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intentToMainActivity)
                    requireActivity().finish()
                } else{
                    navController.navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                }
            }, 1000)
        }else{
            showErrorDialog(resourceProvider.getString(R.string.no_internet_connection)){
                requireActivity().finish()
            }
        }
    }

    private fun isNetworkAvailable() : Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun showErrorDialog(desc: String, callBack: () -> Unit = {}){
        if(errorDialog == null)
            errorDialog = ErrorDialog(requireContext())

        errorDialog?.show(desc, callBack)
    }
}