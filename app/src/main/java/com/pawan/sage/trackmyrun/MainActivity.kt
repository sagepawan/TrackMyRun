package com.pawan.sage.trackmyrun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pawan.sage.trackmyrun.databinding.ActivityMainBinding
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //need to be defined else menu inflater override won't be called in child fragments
        setSupportActionBar(binding.toolbar)

        navigateToTrackingFragmentWhenRequired(intent)

        val navView = binding.bottomNavigationView
        val navController = findNavController(R.id.navHostFragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_run, R.id.navigation_stats, R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    //if activity wasn't destroyed and reused the pending intent to launch
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentWhenRequired(intent)
    }

    //take user to tracking fragment once MainActivity is relaunched from clicking notification
    private fun navigateToTrackingFragmentWhenRequired(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navController.navigate(R.id.action_global_trackingFragment)
        }
    }

}