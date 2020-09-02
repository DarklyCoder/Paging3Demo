package com.darklycoder.paging3demo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.darklycoder.paging3demo.data.User

class UserAdapter : PagingDataAdapter<User, UserAdapter.VH>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.uid == newItem.uid
            override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(parent)

    class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
    ) {

        private val tvName = itemView.findViewById<TextView>(R.id.tv_name)

        fun bind(item: User?) {
            tvName.text = item?.name
        }
    }

}