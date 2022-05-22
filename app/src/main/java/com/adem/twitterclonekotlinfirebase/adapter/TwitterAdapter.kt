package com.adem.twitterclonekotlinfirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adem.twitterclonekotlinfirebase.R
import com.adem.twitterclonekotlinfirebase.databinding.RecyclerRowBinding
import com.adem.twitterclonekotlinfirebase.model.TwitterModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class TwitterAdapter (val twitterArray:ArrayList<TwitterModel>): RecyclerView.Adapter<TwitterAdapter.TwitterHolder>() {

    private lateinit var firestore:FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    class TwitterHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwitterHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TwitterHolder(binding)

    }

    override fun onBindViewHolder(holder: TwitterHolder, position: Int) {

        if (twitterArray.get(position).tweetOwner!=null){

            retweetOption(holder.binding,position)

        }else{

            holder.binding.retweetOwner.visibility=View.GONE
            holder.binding.homeRetweetImageView.visibility=View.GONE

            holder.binding.recyEmailText.text = twitterArray.get(position).mail
            holder.binding.recyTweetText.text = twitterArray.get(position).tweet
            holder.binding.recyDate.text = (twitterArray.get(position).date.toDate()).toString()

            twitterArray.get(position).downloadUrl?.let {

                holder.binding.homeImageView.isVisible = true
                Picasso.get().load(twitterArray[position].downloadUrl).into(holder.binding.homeImageView)

            }
        }


        likeButton(binding = holder.binding)
        reTweet(holder.binding, position)



    }

    override fun getItemCount(): Int {
       return twitterArray.size
    }

    fun likeButton(binding: RecyclerRowBinding){
        var number =0

        binding.likeButton.setOnClickListener(){

            if (number==0){

                binding.likeButton.setImageResource(R.drawable.heart)
                number=1

            }
            else if (number==1){

                binding.likeButton.setImageResource(R.drawable.like)
                number=0

            }
        }


    }


    fun reTweet(binding: RecyclerRowBinding,position: Int){


        firestore=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        storage=FirebaseStorage.getInstance()

        binding.reTweetButton.setOnClickListener(){

            val tweetOwner=twitterArray.get(position).mail
            val tweet=twitterArray.get(position).tweet
            val downloadUrl=twitterArray.get(position).downloadUrl


            val reTweetHashMap= hashMapOf<String,Any>()
            reTweetHashMap.put("Email",(auth.currentUser!!.email).toString())
            reTweetHashMap.put("TweetOwner",tweetOwner)
            reTweetHashMap.put("Tweet",tweet)
            reTweetHashMap.put("Date",Timestamp.now())
            downloadUrl?.let {
                reTweetHashMap.put("DownloadUrl",downloadUrl)
            }



            firestore.collection("Tweets").add(reTweetHashMap).addOnSuccessListener {

                Toast.makeText(binding.root.context, "Retweet success", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {

                Toast.makeText(binding.root.context, it.message, Toast.LENGTH_LONG).show()
            }


        }
    }


    fun retweetOption(binding: RecyclerRowBinding,position:Int){



            binding.retweetOwner.isVisible=true
            binding.homeRetweetImageView.isVisible=true
            binding.retweetOwner.text="${twitterArray.get(position).mail} Retweeted"
            binding.recyEmailText.text = twitterArray.get(position).tweetOwner
            binding.recyTweetText.text = twitterArray.get(position).tweet
            binding.recyDate.text= (twitterArray.get(position).date.toDate()).toString()

            twitterArray.get(position).downloadUrl?.let {

                binding.homeImageView.isVisible=true
                Picasso.get().load(twitterArray[position].downloadUrl).into(binding.homeImageView)


        }

    }




}