package com.example.data_recovery

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.data_recovery.adopters.UserAdopter
import com.example.data_recovery.databinding.RecycleBinding
import com.example.data_recovery.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Recycler : AppCompatActivity() {
    lateinit var binding: RecycleBinding
    lateinit var userAdopter: MyRecyclerAdapter
    val dataList = arrayListOf(
        UserModel(name = "Ali Usman", age = "28"),
        UserModel(name = "Umar", age = "200230048"),
        UserModel(name = "ALi", age = "200230048"),
        UserModel(name = "Umar", age = "200230048"),
        UserModel(name = "Umar", age = "200230048")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.recycle)
        userAdopter = MyRecyclerAdapter { user -> adopterOnClick(user) }
        binding.recylerID.adapter = userAdopter


        userAdopter.submitList(dataList)
        userAdopter.notifyDataSetChanged()
    }


    fun adopterOnClick(user: UserModel) {

    }
}