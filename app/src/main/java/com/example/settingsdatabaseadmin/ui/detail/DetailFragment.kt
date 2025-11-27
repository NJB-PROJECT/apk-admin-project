package com.example.settingsdatabaseadmin.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.settingsdatabaseadmin.databinding.FragmentDetailBinding
import com.example.settingsdatabaseadmin.model.AppConfig

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logAdapter = LogAdapter()
        binding.logsRecyclerView.adapter = logAdapter
        binding.logsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)


        viewModel.app.observe(viewLifecycleOwner) { app ->
            app?.let {
                binding.versionCodeEditText.setText(it.config.latestVersionCode.toString())
                binding.versionNameEditText.setText(it.config.latestVersionName)
                binding.maintenanceCodeEditText.setText(it.config.maintenanceCode)
                binding.isActiveSwitch.isChecked = it.config.isActive
                binding.maintenanceModeSwitch.isChecked = it.config.maintenanceMode
                logAdapter.submitList(it.logs)
            }
        }

        viewModel.loadApp(args.packageName)

        binding.saveButton.setOnClickListener {
            val newConfig = AppConfig(
                latestVersionCode = binding.versionCodeEditText.text.toString().toIntOrNull() ?: 0,
                latestVersionName = binding.versionNameEditText.text.toString(),
                maintenanceCode = binding.maintenanceCodeEditText.text.toString(),
                isActive = binding.isActiveSwitch.isChecked,
                maintenanceMode = binding.maintenanceModeSwitch.isChecked
            )
            viewModel.updateConfig(args.packageName, newConfig, "Configuration updated from admin app.")
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete App")
            .setMessage("Are you sure you want to delete this application's configuration?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteApp(args.packageName)
                findNavController().popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
