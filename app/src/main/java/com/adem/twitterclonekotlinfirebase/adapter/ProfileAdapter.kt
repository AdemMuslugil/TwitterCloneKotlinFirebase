package com.adem.twitterclonekotlinfirebase.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adem.twitterclonekotlinfirebase.R
import com.adem.twitterclonekotlinfirebase.databinding.FragmentProfileBinding
import com.adem.twitterclonekotlinfirebase.databinding.ProfileRecycRowBinding
import com.adem.twitterclonekotlinfirebase.databinding.RecyclerRowBinding
import com.adem.twitterclonekotlinfirebase.model.TwitterModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileAdapter(val tweetArray:ArrayList<TwitterModel>): RecyclerView.Adapter<ProfileAdapter.ProfileHolder>() {

    private lateinit var firestore: FirebaseFirestore

    class ProfileHolder(val binding: ProfileRecycRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHolder {

        val binding=ProfileRecycRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProfileHolder(binding)

    }

    override fun onBindViewHolder(holder: ProfileHolder, position: Int) {

        holder.binding.recyEmailText.text= tweetArray[position].mail
        holder.binding.recyTweetText.text=tweetArray[position].tweet
        holder.binding.recyDate.text=(tweetArray[position].date.toDate()).toString()

        if (tweetArray.get(position).downloadUrl!=null) {

            holder.binding.profileImageView.isVisible=true
            Picasso.get().load(tweetArray.get(position).downloadUrl).into(holder.binding.profileImageView)

        }else {

            holder.binding.profileImageView.visibility=View.GONE

        }


        likeButton(binding = holder.binding)



        if (tweetArray.get(position).tweetOwner!=null){

            retweetOption(holder.binding,position)

        }else{

            holder.binding.profileRetweetImageView.visibility=View.GONE
            holder.binding.retweetOwner.visibility=View.GONE


        }
        optionMenu(holder.binding,position)
    }

    override fun getItemCount(): Int {
        return tweetArray.size
    }


    fun likeButton(binding: ProfileRecycRowBinding){

        var number=0

        binding.likeButton.setOnClickListener(){

            if (number==0){

                binding.likeButton.setImageResource(R.drawable.heart)
                number=1


            }else if(number==1){

                binding.likeButton.setImageResource(R.drawable.like)
                number=0

            }

        }

    }


    fun retweetOption(binding: ProfileRecycRowBinding,position:Int){



        binding.retweetOwner.isVisible=true
        binding.profileRetweetImageView.isVisible=true
        binding.retweetOwner.text="You Retweeted"

        binding.recyEmailText.text = tweetArray.get(position).tweetOwner
        binding.recyTweetText.text = tweetArray.get(position).tweet
        binding.recyDate.text= (tweetArray.get(position).date.toDate()).toString()

        tweetArray.get(position).downloadUrl?.let {

            binding.profileImageView.isVisible=true
            Picasso.get().load(tweetArray[position].downloadUrl).into(binding.profileImageView)


        }

    }





    fun optionMenu(binding: ProfileRecycRowBinding,position: Int){

            binding.optionsImage.setOnClickListener(){
            firestore=FirebaseFirestore.getInstance()
            val tweet=tweetArray.get(position).tweet
            val query=firestore.collection("Tweets").whereEqualTo("Tweet",tweet).get()

            query.addOnSuccessListener {

                for (doc in it){

                    firestore.collection("Tweets").document(doc.id).delete().addOnSuccessListener {

                        Toast.makeText(binding.root.context, "Tweet Deleted", Toast.LENGTH_SHORT).show()

                    }

                }

            }

        }

    }

}