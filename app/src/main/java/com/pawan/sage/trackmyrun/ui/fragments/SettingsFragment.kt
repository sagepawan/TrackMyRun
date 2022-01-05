package com.pawan.sage.trackmyrun.ui.fragments


import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.FragmentSettingsBinding
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_NAME
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadChangesFromSharedPref()

        binding.btnApplyChanges.setOnClickListener{
            val success = applyChangesToSharedPrefs()

            if(success){
                Snackbar.make(view, "Changes saved!", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill out all fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)



        return binding.root
    }

    private fun loadChangesFromSharedPref() {
        val name = sharedPreferences.getString(KEY_NAME, "")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 80f)
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPrefs(): Boolean {
        val nameText = binding.etName.text.toString()
        val weightText = binding.etWeight.text.toString()

        if(nameText.isEmpty()||weightText.isEmpty()){
            return false
        }

        sharedPreferences.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()

        val toolbarText = "Let's go $nameText!"
        requireActivity().findViewById<TextView>(R.id.tvToolbarTitle).text = toolbarText
        return true
    }
}