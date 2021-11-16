package com.example.fakeapi

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.example.fakeapi.MyApp.Companion.instance
import com.example.fakeapi.databinding.ActivityMainBinding
import com.example.fakeapi.retrofit.Post
import com.example.fakeapi.ui.PostsListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

import android.widget.EditText

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts


private const val POSTS = "POSTS"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var posts: ArrayList<Post>
    private lateinit var listAdapter: PostsListAdapter

    private var userId = 12

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                data?.getStringExtra(BODY)?.let { body ->
                    data.getStringExtra(TITLE)?.let { title ->
                        createPost(title, body)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listAdapter = PostsListAdapter {
            lifecycle.coroutineScope.launch {
                try {
                    val resp = instance.postService.deletePost(it.id)
                    if (resp.isSuccessful) {
                        posts.remove(it).toString()
                        withContext(Dispatchers.Main) {
                            listAdapter.submitList(posts.toList())
                        }
                    }
                    showCode(resp.code())
                } catch (e: Exception) {
                    Snackbar.make(binding.root, R.string.trouble_network, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.posts.apply {
            adapter = listAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }

        if (savedInstanceState?.getParcelableArrayList<Post>(POSTS) == null) {
            initPosts()
        } else {
            posts = savedInstanceState.getParcelableArrayList<Post>(POSTS)!!
            listAdapter.submitList(posts.toList())
        }


        binding.addPost.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            resultLauncher.launch(intent)
        }

    }


    private suspend fun showCode(code: Int) = withContext(Dispatchers.Main) {
        Snackbar.make(binding.root, code.toString(), Snackbar.LENGTH_SHORT).show()
    }


    private fun createPost(title: String, body: String) =
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            try {
                val newPost = Post(userId, posts.size, title, body)
                val resp = instance.postService.createPost(newPost)
                if (resp.isSuccessful) {
                    posts.add(0, newPost)
                    listAdapter.submitList(posts.toList())
                }
                showCode(resp.code())
            } catch (e: Exception) {
                Snackbar.make(binding.root, R.string.trouble_network, Snackbar.LENGTH_SHORT).show()
            }
        }


    private fun showDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_create_post)
        val title: EditText = dialog.findViewById(R.id.title)
        val body: EditText = dialog.findViewById(R.id.body)
        val createBtn: Button = dialog.findViewById(R.id.create_button) as Button
        createBtn.setOnClickListener {
            lifecycle.coroutineScope.launch {
                createPost(title.text.toString(), body.text.toString())
                dialog.dismiss()
            }
        }
        val cancelBtn: Button = dialog.findViewById(R.id.cancel_button) as Button
        cancelBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(POSTS, posts)
    }

    private suspend fun getAllPosts(): List<Post> {
        return try {
            val resp = instance.postService.listPosts()
            showCode(resp.code())
            resp.body()!!.reversed()
        } catch (e: Exception) {
            Snackbar.make(binding.root, R.string.trouble_network, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.restart) { initPosts() }
                .show()
            ArrayList<Post>()
        }
    }

    private fun initPosts() {
        lifecycle.coroutineScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                binding.indeterminateBar.visibility = View.VISIBLE
                binding.addPost.visibility = View.GONE
            }

            posts = ArrayList(getAllPosts())

            withContext(Dispatchers.Main) {
                listAdapter.submitList(posts.toList())
                binding.indeterminateBar.visibility = View.GONE
                binding.addPost.visibility = View.VISIBLE
            }
        }
    }

}