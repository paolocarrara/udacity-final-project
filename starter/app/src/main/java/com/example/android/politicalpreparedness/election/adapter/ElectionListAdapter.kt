package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionListViewItemBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(
    private val clickListener: ElectionListener
): ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
        holder.bind(item)
    }

    class ElectionViewHolder(private var binding: ElectionListViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from (parent: ViewGroup): ElectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: ElectionListViewItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.election_list_view_item, parent, false)

                return ElectionViewHolder(binding)
            }
        }

        fun bind (item: Election) {
            binding.election = item
            binding.executePendingBindings()
        }
    }

    class ElectionListener(val clickListener: (item: Election) -> Unit) {
        fun onClick(item: Election) = clickListener(item)
    }

    companion object ElectionDiffCallback: DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }
    }
}