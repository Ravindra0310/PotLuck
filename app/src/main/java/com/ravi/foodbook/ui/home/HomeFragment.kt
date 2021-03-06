package com.ravi.foodbook.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.ravi.foodbook.OnFoodItemClickListener
import com.ravi.foodbook.R
import com.ravi.foodbook.databinding.FragmentHomeBinding
import com.ravi.foodbook.model.FoodModel
import com.ravi.foodbook.model.FoodModelAdapter
import com.ravi.foodbook.ui.user.DetailedPostActivity
import kotlinx.android.synthetic.main.activity_bottom_nav.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.Serializable

class HomeFragment : Fragment(), OnFoodItemClickListener {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var foodModelAdapter: FoodModelAdapter
    lateinit var databaseReference: DatabaseReference
    private var foodModelList: ArrayList<FoodModel>? = null
    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting data to recyclerview from firebase
        setRecyclerData()

        swipeRefreshLayout.setOnRefreshListener {
            setRecyclerData()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun setRecyclerData() {

        foodModelList = arrayListOf<FoodModel>()
        databaseReference = FirebaseDatabase.getInstance().getReference("posts")
        recycler1.setHasFixedSize(true)
        recycler1.layoutManager = LinearLayoutManager(context)
        foodModelAdapter = FoodModelAdapter(foodModelList!!, this)
        recycler1.adapter = foodModelAdapter


        try {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (i in snapshot.children) {
                            val foodList = i.getValue(FoodModel::class.java)

                            foodModelList?.add(foodList!!)
                        }

                        foodModelList!!.reverse()

                        val layoutAnimationController: LayoutAnimationController =
                            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
                        recycler1?.layoutAnimation = layoutAnimationController

                        foodModelAdapter.notifyDataSetChanged()
                        simpleProgressBar?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }catch (e : Exception){}
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(foodModel: FoodModel) {
        val navController = findNavController()
        val bundle = Bundle()

        //navController.navigate(R.id.action_navigation_home_to_detailedPostActivity, bundle)
        val intent = Intent(context,DetailedPostActivity::class.java)
        intent.putExtra("content",foodModel.content)
        intent.putExtra("userNameHome",foodModel.userName)
        intent.putExtra("foodPic",foodModel.foodPic)
        intent.putExtra("foodType",foodModel.foodType)
        intent.putExtra("freshness",foodModel.freshness)
        intent.putExtra("location",foodModel.location)
        intent.putExtra("price",foodModel.price)
        intent.putExtra("time",foodModel.time)
        intent.putExtra("userPic",foodModel.userPic)
        intent.putExtra("userIdHome",foodModel.uid)
        startActivity(intent)

    }
}