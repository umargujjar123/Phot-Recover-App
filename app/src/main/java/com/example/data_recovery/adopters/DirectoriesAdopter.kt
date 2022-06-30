package com.example.data_recovery.adopters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.data_recovery.databinding.DirectoriesfilesBinding
import com.example.data_recovery.databinding.MyRowBinding
import com.example.data_recovery.model.DirectoriesModel
import com.example.data_recovery.model.UserModel

class DirectoriesAdopter(private val onClick: (DirectoriesModel, index: Int) -> Unit) :
    ListAdapter<DirectoriesModel, DirectoriesAdopter.UserViewHolder>(DirectoryDifferentUtil) {

    class UserViewHolder(
        var directoriesBinding: DirectoriesfilesBinding,
        val onClick: (DirectoriesModel, index: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(directoriesBinding.root) {
//        private var directoriesModel: DirectoriesModel? = null
//        init {
//            itemView.setOnClickListener {
//                directoriesModel?.let { directoryModel->
//                    onClick(directoryModel, it.)
//                }
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val userListsRowDesignBinding: DirectoriesfilesBinding =
            DirectoriesfilesBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(
            userListsRowDesignBinding, onClick
        )
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val directory = getItem(position)
        holder.directoriesBinding.directory = directory
        holder.directoriesBinding.root.setOnClickListener {
            directory?.let {
                onClick(it, position)
            }
        }

    }


}


object DirectoryDifferentUtil : DiffUtil.ItemCallback<DirectoriesModel>() {
    override fun areItemsTheSame(oldItem: DirectoriesModel, newItem: DirectoriesModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DirectoriesModel, newItem: DirectoriesModel): Boolean {
//        return oldItem.id == newItem.id
        return false
    }
}
