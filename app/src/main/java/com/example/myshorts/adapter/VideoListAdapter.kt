package com.example.myshorts.adapter

import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myshorts.ProfileActivity
import com.example.myshorts.R
import com.example.myshorts.databinding.VideoItemRowBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.myshorts.model.UserModel
import com.example.myshorts.model.VideoModel
import com.example.myshorts.util.UiUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Color

class VideoListAdapter(options: FirestoreRecyclerOptions<VideoModel>) : FirestoreRecyclerAdapter<VideoModel, VideoListAdapter.VideoViewHolder>(options) {

    inner class VideoViewHolder(private val binding: VideoItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isRed = false

        fun bindVideo(videoModel: VideoModel) {
            loadUserData(videoModel.uploaderId)

            binding.captionView.text = videoModel.title
            binding.progressBar.visibility = View.VISIBLE

            binding.videoView.apply {
                setVideoPath(videoModel.url)
                setOnPreparedListener {
                    binding.progressBar.visibility = View.GONE
                    binding.pauseIcon.visibility = View.GONE
                    it.start()
                    it.isLooping = true
                }


                setOnClickListener {
                    if (isPlaying) {
                        pause()
                        binding.pauseIcon.visibility = View.VISIBLE
                    } else {
                        start()
                        binding.pauseIcon.visibility = View.GONE
                    }
                }


                binding.like.setOnClickListener {
                    if (isRed)
                    {
                       binding.like.setImageResource(R.drawable.favorite_icon)

                    }
                    else
                    {
                        binding.like.setImageResource(R.drawable.liked)

                    }
                    isRed = !isRed
                }


            }
        }



        private fun loadUserData(uploaderId: String) {
            Firebase.firestore.collection("users")
                .document(uploaderId)
                .get().addOnSuccessListener { documentSnapshot ->
                    val userModel = documentSnapshot.toObject(UserModel::class.java)
                    userModel?.apply {
                        binding.usernameView.text = username

                        Glide.with(binding.profileIcon).load(profilePic)
                            .circleCrop()
                            .apply(RequestOptions().placeholder(R.drawable.icon_profile))
                            .into(binding.profileIcon)

                        binding.userDetailLayout.setOnClickListener {
                            val intent = Intent(binding.userDetailLayout.context, ProfileActivity::class.java)
                            intent.putExtra("profile_user_id", id)
                            binding.userDetailLayout.context.startActivity(intent)
                        }


                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = VideoItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: VideoModel) {
        holder.bindVideo(model)
    }
}