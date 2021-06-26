package com.ravi.foodbook.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codingwithme.firebasechat.model.User
import com.example.jobedin.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.ravi.foodbook.R
import com.ravi.foodbook.databinding.FragmentChatBinding
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat_message.*

class ChatFragment : Fragment(), OnClickListner {
    private var _binding: FragmentChatBinding? = null
    lateinit var repository: ChatRepository
    lateinit var navController: NavController
    lateinit var userAdapter:ChatLIstAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var userList = ArrayList<User>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=findNavController()
        userRecyclerView.layoutManager = LinearLayoutManager(context )
        val userAdapter = ChatLIstAdapter(requireContext(), userList,this@ChatFragment)
        userRecyclerView.adapter = userAdapter
        getUsersList()
    }

    fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == "") {
                   // imgProfile.setImageResource(R.drawable.profile_image)
                } else {
                    Glide.with(imgProfile).load(currentUser.profileImage).into(imgProfile)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {

                        userList.add(user)
                    }

                }
                userAdapter.notifyDataSetChanged()


            }

        })
    }

    override fun getdataToMessage(bundle: Bundle, position: Int) {
        navController.navigate(R.id.action_navigation_chat_to_chatMessage,bundle)
    }
}