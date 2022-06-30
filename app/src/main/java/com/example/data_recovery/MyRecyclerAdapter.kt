package com.example.data_recovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.data_recovery.adopters.TeacherDiffCallback
import com.example.data_recovery.databinding.MyRowBinding
import com.example.data_recovery.model.UserModel

class MyRecyclerAdapter(private val onClick: (UserModel) -> Unit) :
    ListAdapter<UserModel, MyRecyclerAdapter.UserViewHolder>(
        TeacherDiffCallback
    ) {
        class UserViewHolder(
     var myRowBinding: MyRowBinding,
     var onClick: (UserModel) -> Unit
        ):RecyclerView.ViewHolder(myRowBinding.root){
            private var userModel: UserModel? = null
             init {
                 itemView.setOnClickListener(){
                     userModel?.let{
                         (onClick(it))
                     }
                 }
             }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerAdapter.UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val userListsRowDesignBinding: MyRowBinding = MyRowBinding.inflate(layoutInflater, parent, false)
        return MyRecyclerAdapter.UserViewHolder(
            userListsRowDesignBinding, onClick
        )
    }

    override fun onBindViewHolder(holder: MyRecyclerAdapter.UserViewHolder, position: Int) {
        val day = getItem(position)
        holder.myRowBinding.user = day
        holder.myRowBinding.rowID.setOnClickListener(
            View.OnClickListener {
                day?.let {
                    onClick(it)
                }
            })

    }
}