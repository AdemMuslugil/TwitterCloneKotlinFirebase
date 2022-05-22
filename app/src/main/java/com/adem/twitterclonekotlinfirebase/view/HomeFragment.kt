package com.adem.twitterclonekotlinfirebase.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.adem.twitterclonekotlinfirebase.R
import com.adem.twitterclonekotlinfirebase.adapter.TwitterAdapter
import com.adem.twitterclonekotlinfirebase.databinding.FragmentHomeBinding
import com.adem.twitterclonekotlinfirebase.databinding.RecyclerRowBinding
import com.adem.twitterclonekotlinfirebase.model.TwitterModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.ticker


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var twitterArray: ArrayList<TwitterModel>
    private lateinit var adapter: TwitterAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fireStore = FirebaseFirestore.getInstance()

        twitterArray = ArrayList()

        getData()
        addButton()
        optionRecyclerView()
        goProfile()
        goHomePage()

    }


    fun addButton() {

        binding.fobButton.setOnClickListener() {
            val action = HomeFragmentDirections.actionHomeFragmentToPostFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }


    fun goProfile() {

        binding.userImage.setOnClickListener() {

            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            Navigation.findNavController(it).navigate(action)

        }

    }


    fun getData() {
        fireStore.collection("Tweets").orderBy("Date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->


                if (error != null) {

                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()

                } else {

                    if (value != null) {


                        twitterArray.clear()

                        val document = value.documents

                        for (document in document) {

                            val email = document.get("Email") as String
                            val tweet = document.get("Tweet") as String
                            val date = document.get("Date") as Timestamp
                            val url = document.get("DownloadUrl") as String?
                            val tweetOwner=document.get("TweetOwner") as String?

                            val posts = TwitterModel(email, tweet, date, url,tweetOwner)
                            twitterArray.add(posts)

                        }
                        adapter.notifyDataSetChanged()

                    }
                }

            }

    }


    fun goHomePage(){

        binding.homeImage.setOnClickListener(){

            binding.recyclerView.smoothScrollToPosition(0)

        }


    }



    fun optionRecyclerView() {

        adapter = TwitterAdapter(twitterArray)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

}
