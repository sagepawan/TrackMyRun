package com.pawan.sage.trackmyrun.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavOptions.Builder
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.FragmentSetupBinding
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_FIRST_TIME_TOGGLE
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_NAME
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: FragmentSetupBinding

    @set:Inject
    var isFirstTimeRunningApp = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSetupBinding.inflate(inflater, container, false)

        //to check if prefs haven't been saved
        //if saved, pop setup fragment from stack so that when user presses back from run fragment
        //user does not come back to setup fragment
        if(!isFirstTimeRunningApp){
            val navOptions = Builder()
                .setPopUpTo(R.id.navigation_setup, true)
                .build()

            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )

        }

        binding.tvContinue.setOnClickListener{

            val success = writeDataToSharedPref()

            if(success){
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all fields", Snackbar.LENGTH_SHORT).show()
            }

        }

        return binding.root

    }

    //return boolean to check if accessing shared prefs works or not
    private fun writeDataToSharedPref(): Boolean{
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()

        if(name.isEmpty()||weight.isEmpty()){
            return false
        }

        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbarText = "Let's go, $name!"
        requireActivity().findViewById<TextView>(R.id.tvToolbarTitle).text = toolbarText

        return true

    }

}