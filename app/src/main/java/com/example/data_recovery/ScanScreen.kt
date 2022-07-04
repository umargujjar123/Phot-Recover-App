package com.example.data_recovery

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.data_recovery.Constants.Common
import com.example.data_recovery.adopters.DirectoriesAdopter
import com.example.data_recovery.databinding.ScanScreenBinding
import com.example.data_recovery.model.DirList
import com.example.data_recovery.model.DirectoriesModel

class ScanScreen : AppCompatActivity() {
    lateinit var binding: ScanScreenBinding

    lateinit var directoryAdaptor: DirectoriesAdopter
    lateinit var imagesList: DirList
    val imageList: MutableList<DirectoriesModel> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.scan_screen)

        imagesList = intent.getSerializableExtra("dataList") as DirList

        Log.e("TAG", "The data is ${Common.imageslist}")

//        val imagesList: ArrayList<DirectoriesModel>? = data!!.getParcelable<Parcelable>("dataList") as ArrayList<DirectoriesModel>?
//        Log.e("TAG", "onCreate:  the list data is ${imagesList.toString()}", )

//
        directoryAdaptor =
            DirectoriesAdopter({ directoryModel, index -> imageItemOnClick(directoryModel, index) },
                { directoryModel, index, isSelected ->
                    selectorOnClick(
                        directoryModel,
                        index,
                        isSelected
                    )
                })


        val layoutManager = GridLayoutManager(this@ScanScreen, 3)
        binding.directoryID2.layoutManager = layoutManager
        binding.directoryID2.adapter = directoryAdaptor

        directoryAdaptor.submitList(Common.imageslist)
        directoryAdaptor.notifyDataSetChanged()


    }

    private fun imageItemOnClick(directoriesModel: DirectoriesModel, index: Int) {
        Log.e(
            "TAG",
            "imageItemOnClick: clicked image path is --> ${directoriesModel.image} and index is $index",
        )
        val b = Bundle().apply {
            putSerializable(DIRECTORY_MODEL_ARGS, directoriesModel)
        }
        startActivityForResult(
            Intent(this, ImageView::class.java).putExtras(b).putExtra("index", index), 101
        )
    }

    private fun selectorOnClick(
        directoriesModel: DirectoriesModel,
        index: Int,
        isSelected: Boolean
    ) {
        if(isSelected){
            imageList.add(DirectoriesModel(name = directoriesModel.name, image = directoriesModel.image ) )
            Log.e("TAG", "selectorOnClick: The images added in the model are as ${imageList}", )
        }else{
            imageList.remove(DirectoriesModel(name = directoriesModel.name, image = directoriesModel.image ))
            Log.e("TAG", "selectorOnClick: The images removed in the model are as ${imageList}", )

        }
        Log.e(
            "TAG",
            "imageItemOnClick: clicked image index is --> $index and the selection value is $isSelected",
        )

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAG", "onActivityResult: The value of the request code is${requestCode}")
        Log.e("TAG", "onActivityResult: The Value of the reult code is ${resultCode}")
        if (requestCode == 101) {
            val deletedIndex: Int = data?.extras?.getInt("Deleted_Index") ?: -1
            Log.e("TAG", "onActivityResult: THe coming index is ${deletedIndex}")
            if (deletedIndex > -1) {
                Log.e("TAG", "onActivityResult: The Value of the code is ${requestCode}")
                imagesList.directories.removeAt(deletedIndex)
                Log.e("TAG", "onActivityResult: The latest image list is ${imagesList}")
                if (this@ScanScreen::directoryAdaptor.isInitialized) {
                    directoryAdaptor.submitList(imagesList.directories)
                    directoryAdaptor.notifyDataSetChanged()
//                    directoryAdaptor.notifyItemChanged(deletedIndex)
                }
            }
        }
    }

    companion object {
        val DIRECTORY_MODEL_ARGS = "DIRECTORY_MODEL_ARGS"
    }
}