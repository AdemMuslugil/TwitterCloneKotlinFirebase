package com.adem.twitterclonekotlinfirebase.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.adem.twitterclonekotlinfirebase.databinding.FragmentPostBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var selectedUri: Uri?=null
    private lateinit var storage:FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(layoutInflater)
        val view = binding.root
        return view


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firestore=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        storage=FirebaseStorage.getInstance()



        cancel()
        tweet()
        ProfileImageClick()
        SelectImage()
        RegisterLauncher()


    }


    fun tweet() {
        binding.tweetButton.setOnClickListener {
            //tweet send

            if (selectedUri!=null){

                ImageTweets()

            }
            else {

                val tweet = binding.postEditText.text.toString()

                val tweetMap = hashMapOf<String, Any>()
                tweetMap.put("Email", auth.currentUser!!.email!!)
                tweetMap.put("Tweet", tweet)
                tweetMap.put("Date", Timestamp.now())



                firestore.collection("Tweets").add(tweetMap).addOnSuccessListener {

                    val action = PostFragmentDirections.actionPostFragmentToHomeFragment()
                    Navigation.findNavController(binding.root).navigate(action)

                    Toast.makeText(context, "Tweet is shared", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {

                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }


            }


            }

        }


    fun ImageTweets(){

        val storageRef = storage.reference
        val uuid = UUID.randomUUID() //random name create
        val imageName = "$uuid.png"
        val imageRef = storageRef.child("Images/$imageName")

        if (selectedUri != null) {

            imageRef.putFile(selectedUri!!).addOnSuccessListener() {

                val uploadImageRef = storage.reference.child("Images/$imageName")
                uploadImageRef.downloadUrl.addOnSuccessListener {

                    val downloadUrl = it.toString()

                    val tweet = binding.postEditText.text.toString()

                    val tweetMap = hashMapOf<String, Any>()
                    tweetMap.put("Email", auth.currentUser!!.email!!)
                    tweetMap.put("Tweet", tweet)
                    tweetMap.put("Date", Timestamp.now())
                    tweetMap.put("DownloadUrl", downloadUrl)




                    firestore.collection("Tweets").add(tweetMap).addOnSuccessListener {

                        val action = PostFragmentDirections.actionPostFragmentToHomeFragment()
                        Navigation.findNavController(binding.root).navigate(action)

                        Toast.makeText(context, "Tweet is shared", Toast.LENGTH_SHORT).show()


                    }.addOnFailureListener {

                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    }


                }


            }


        }

    }




    fun ProfileImageClick(){

        binding.postUserImageView.setOnClickListener(){

            val action=PostFragmentDirections.actionPostFragmentToProfileFragment()
            Navigation.findNavController(it).navigate(action)

        }

    }


    fun SelectImage() {

        binding.selectImageView.setOnClickListener() {

            if (ContextCompat.checkSelfPermission(
                    binding.root.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //snackbar
                    Snackbar.make(binding.root, "Permission Needed!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                        }.show()


                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }

            } else {
                //permission granted & go to gallery
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }

        }

    }



    fun RegisterLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->

            if (result.resultCode==RESULT_OK){

                val intentToGallery=result.data

                if (intentToGallery!=null){

                    selectedUri=intentToGallery.data
                    selectedUri?.let{

                        binding.showPostImageView.isVisible=true
                        binding.showPostImageView.setImageURI(selectedUri)

                    }

                }

            }

        }


        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->

            if (result){

                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{

                Toast.makeText(binding.root.context, "Permission Needed!", Toast.LENGTH_LONG).show()

            }

        }


    }


    fun cancel(){
        binding.cancelText.setOnClickListener(){
            val action=PostFragmentDirections.actionPostFragmentToHomeFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }
}

