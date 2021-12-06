package com.pawan.sage.trackmyrun.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.FragmentSetupBinding


class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSetupBinding.inflate(inflater, container, false)

        binding.tvContinue.setOnClickListener{
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }

        return binding.root

    }

}