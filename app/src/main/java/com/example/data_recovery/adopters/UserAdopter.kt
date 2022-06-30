package com.example.data_recovery.adopters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.data_recovery.databinding.MyRowBinding
import com.example.data_recovery.model.UserModel

class UserAdopter(private val onClick: (UserModel) -> Unit) :
    ListAdapter<UserModel, UserAdopter.UserViewHolder>(TeacherDiffCallback) {

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class UserViewHolder(
        var myRowBinding: MyRowBinding,
        val onClick: (UserModel) -> Unit
    ) :
        RecyclerView.ViewHolder(myRowBinding.root) {
        private var userModel: UserModel? = null
        init {
            itemView.setOnClickListener {
                userModel?.let {
                    onClick(it)
                }
            }
        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val userListsRowDesignBinding: MyRowBinding = MyRowBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(
            userListsRowDesignBinding, onClick
        )
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
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

object TeacherDiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
//        return oldItem.id == newItem.id
        return false
    }
}