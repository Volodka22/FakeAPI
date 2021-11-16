package com.example.fakeapi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeapi.databinding.PostCardBinding
import com.example.fakeapi.retrofit.Post


class PostsListAdapter(
    private val onClickDelete: (Post) -> Unit,
) : ListAdapter<Post, PostsListAdapter.PostViewHolder>(PostDiffUtilCallback()) {

    class PostViewHolder(postCardBinding: PostCardBinding) :
        RecyclerView.ViewHolder(postCardBinding.root) {

        private val title = postCardBinding.title
        private val body = postCardBinding.body
        val delButton = postCardBinding.delButton

        fun bind(post: Post) {
            title.text = post.title
            body.text = post.body
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemContactBinding = PostCardBinding.inflate(layoutInflater, parent, false)
        val holder = PostViewHolder(itemContactBinding)
        holder.delButton.setOnClickListener {
            if (holder.absoluteAdapterPosition != -1) {
                onClickDelete(getItem(holder.absoluteAdapterPosition))
            }
        }
        return holder
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) = holder.bind(getItem(position))

}