package com.adem.twitterclonekotlinfirebase.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.adem.twitterclonekotlinfirebase.R
import com.adem.twitterclonekotlinfirebase.adapter.ProfileAdapter
import com.adem.twitterclonekotlinfirebase.databinding.FragmentProfileBinding
import com.adem.twitterclonekotlinfirebase.model.TwitterModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var twitterArray: ArrayList<TwitterModel>
    private lateinit var adapter:ProfileAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(layoutInflater)
        val view=binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()

        twitterArray= ArrayList()

        getData()
        optionRecyler()
        goHomePage()


    }

    fun getData() {

        val user = auth.currentUser


        user?.let {

            val email=user.email
            binding.profileUserText.text=email

            firestore.collection("Tweets").orderBy("Date",Query.Direction.DESCENDING).whereEqualTo("Email",email).addSnapshotListener { value, error ->

                if (value!=null){

                    twitterArray.clear()

                    val doc= value.documents


                    for (doc in doc){

                        val email=doc.get("Email") as String
                        val tweet=doc.get("Tweet") as String
                        val date=doc.get("Date") as Timestamp
                        val downloadUrl=doc.get("DownloadUrl") as String?
                        val tweetOwner=doc.get("TweetOwner") as String?


                        val twitter=TwitterModel(email,tweet,date,downloadUrl,tweetOwner)
                        twitterArray.add(twitter)

                    }
                    adapter.notifyDataSetChanged()

                }else if (error!=null){

                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                    println(error.message)

                }

            }
        }


    }



    fun goHomePage(){

        binding.homeImage.setOnClickListener(){

            val action=ProfileFragmentDirections.actionProfileFragmentToHomeFragment()
            Navigation.findNavController(it).navigate(action)

        }

    }


    fun optionRecyler(){

        adapter= ProfileAdapter(tweetArray = twitterArray)
        binding.profileRecyclerView.layoutManager=LinearLayoutManager(context)
        binding.profileRecyclerView.adapter=adapter

    }

}