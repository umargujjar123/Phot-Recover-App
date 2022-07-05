package com.example.data_recovery.adopters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data_recovery.databinding.DirectoriesfilesBinding
import com.example.data_recovery.model.DirectoriesModel

class DirectoriesAdopter(
    private val onClick: (DirectoriesModel, index: Int) -> Unit,
    private val onClickSelector: (DirectoriesModel, index: Int, isSelected: Boolean) -> Unit
) :
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
        Glide.with(holder.directoriesBinding.image.context).load(directory.image)
            .into(holder.directoriesBinding.image)
        holder.directoriesBinding.imagee.setOnClickListener {
            directory?.let {
                onClick(it, position)
            }
        }

        holder.directoriesBinding.selection.setOnClickListener {

            holder.directoriesBinding.directory?.isSelected =
                !(holder.directoriesBinding.directory?.isSelected ?: false)
            holder.directoriesBinding.selection.isSelected =
                holder.directoriesBinding.directory?.isSelected ?: false

            onClickSelector(directory, position, holder.directoriesBinding.selection.isSelected)
        }

        holder.directoriesBinding.selection.isSelected =
            holder.directoriesBinding.directory?.isSelected ?: false
        holder.directoriesBinding.executePendingBindings()
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
