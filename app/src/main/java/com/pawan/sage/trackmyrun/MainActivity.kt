package com.pawan.sage.trackmyrun

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pawan.sage.trackmyrun.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val navController = findNavController(R.id.navHostFragment)

        navView.setupWithNavController(navController)



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.runFragment, R.id.statsFragment, R.id.settingsFragment
            )
        )*/

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

}