package com.ravi.foodbook

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance();
        btJoinContinue.setOnClickListener {
            var email = EtJoinEmail.text.toString().trim()
            var password = EtJoinPass.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                EtJoinEmail.error="Invalid Email"
                EtJoinEmail.isFocusable=true
            }else if(password.length <6){
                EtJoinPass.error="Password Length must be 6 characters"
                EtJoinPass.isFocusable=true
            }else{
                registerUser(email,password)
            }
        }


    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    var email=user!!.email
                    var uid=user.uid

                    val hashMap:HashMap<Any,String> = HashMap<Any,String>()
                    hashMap.put("email", email!!)
                    hashMap.put("uid", uid!!)
                    hashMap.put("name", "")
                    hashMap.put("phone", "")
                    hashMap.put("image", "")

                    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val reference: DatabaseReference =database.getReference("Users")

                    reference.child(uid).setValue(hashMap)

                    startActivity(Intent(this,MainActivity::class.java) .setFlags (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}