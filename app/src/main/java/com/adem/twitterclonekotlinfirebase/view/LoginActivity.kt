package com.adem.twitterclonekotlinfirebase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.adem.twitterclonekotlinfirebase.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()


        val user = auth.currentUser


        if (user != null) {//user control

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

    }


    fun signIn(view: View) {

        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()


            }.addOnFailureListener {

                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

            }


        } else {

            Toast.makeText(this, "Enter E-mail & Password", Toast.LENGTH_SHORT).show()

        }
    }


    fun signUp(view: View) {

        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {


            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(this) {

                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()


            }.addOnFailureListener(this) {

                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

            }


        }else {

            Toast.makeText(this , "Enter E-mail & Password", Toast.LENGTH_SHORT).show()

        }
    }
}

