package com.example.settingsdatabaseadmin.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.settingsdatabaseadmin.databinding.ItemAppBinding
import com.example.settingsdatabaseadmin.model.ManagedApp

class AppListAdapter(private val onItemClicked: (ManagedApp) -> Unit) :
    ListAdapter<ManagedApp, AppListAdapter.AppViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class AppViewHolder(private val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: ManagedApp) {
            binding.packageNameTextView.text = app.packageName
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ManagedApp>() {
            override fun areItemsTheSame(oldItem: ManagedApp, newItem: ManagedApp): Boolean {
                return oldItem.packageName == newItem.packageName
            }

            override fun areContentsTheSame(oldItem: ManagedApp, newItem: ManagedApp): Boolean {
                return oldItem == newItem
            }
        }
    }
}
