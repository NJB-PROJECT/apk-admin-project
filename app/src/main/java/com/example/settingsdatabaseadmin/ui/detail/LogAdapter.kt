package com.example.settingsdatabaseadmin.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.settingsdatabaseadmin.databinding.ItemLogBinding
import com.example.settingsdatabaseadmin.model.LogEntry
import java.text.SimpleDateFormat
import java.util.*

class LogAdapter : ListAdapter<LogEntry, LogAdapter.LogViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LogViewHolder(private val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat.getDateTimeInstance()

        fun bind(log: LogEntry) {
            binding.logMessageTextView.text = log.message
            binding.logTimestampTextView.text = dateFormat.format(Date(log.timestamp))
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LogEntry>() {
            override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean {
                return oldItem == newItem
            }
        }
    }
}
