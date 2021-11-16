package com.example.fakeapi.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.fakeapi.retrofit.Post

class PostDiffUtilCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) =
        oldItem == newItem
}
