package com.ejemplomvvm.navigationcomponentexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController


class FirstFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_first, container, false)
        val btnNavigator = root.findViewById<Button>(R.id.btnNavigator)
        btnNavigator.setOnClickListener {
            findNavController().navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment(name = "Lycaon97"))
        }

        return root


    }



}