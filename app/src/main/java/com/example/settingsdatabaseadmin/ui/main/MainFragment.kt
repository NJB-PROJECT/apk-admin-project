package com.example.settingsdatabaseadmin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.settingsdatabaseadmin.R
import com.example.settingsdatabaseadmin.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AppListAdapter { app ->
            val action = MainFragmentDirections.actionMainFragmentToDetailFragment(app.packageName)
            findNavController().navigate(action)
        }
        binding.appsRecyclerView.adapter = adapter

        viewModel.apps.observe(viewLifecycleOwner) { apps ->
            adapter.submitList(apps)
        }

        binding.fabAddApp.setOnClickListener {
            showAddAppDialog()
        }
    }

    private fun showAddAppDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "com.example.newapp"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Add New App")
            .setMessage("Enter the package name:")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val packageName = editText.text.toString()
                if (packageName.isNotBlank()) {
                    viewModel.addApp(packageName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
