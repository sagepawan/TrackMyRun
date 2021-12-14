package com.pawan.sage.trackmyrun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
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

        navigateToTrackingFragmentWhenRequired(intent)

        //setSupportActionBar(binding.toolbar)

       // val navController = findNavController(R.id.navHostFragment)

       // binding.bottomNavigationView.setupWithNavController(navController)

        /*navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.settingsFragment, R.id.runFragment, R.id.statsFragment ->
                    binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }*/

        val navView: BottomNavigationView = binding.bottomNavigationView

        navController = findNavController(R.id.navHostFragment)

        navView.setupWithNavController(navController)



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.runFragment, R.id.statsFragment, R.id.settingsFragment
            )
        )

        /*navController.addOnDestinationChangedListener{ _, destination, _ ->
            when(destination.id){
                R.id.runFragment, R.id.statsFragment, R.id.settingsFragment -> navView.visibility = View.VISIBLE
                else -> navView.visibility = View.GONE
            }

            Log.d(R.id.runFragment.toString()+" - ", destination.id.toString())
        }*/

        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)

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