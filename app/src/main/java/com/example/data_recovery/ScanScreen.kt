package com.example.data_recovery

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.data_recovery.adopters.DirectoriesAdopter
import com.example.data_recovery.databinding.ScanScreenBinding
import com.example.data_recovery.model.DirList
import com.example.data_recovery.model.DirectoriesModel

class ScanScreen: AppCompatActivity() {
    lateinit var binding:ScanScreenBinding

    lateinit var directoryAdaptor: DirectoriesAdopter
    lateinit var imagesList: DirList
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.scan_screen)

        imagesList = intent.getSerializableExtra("dataList") as DirList

        Log.e("TAG", "The data is ${imagesList}", )

//        val imagesList: ArrayList<DirectoriesModel>? = data!!.getParcelable<Parcelable>("dataList") as ArrayList<DirectoriesModel>?
//        Log.e("TAG", "onCreate:  the list data is ${imagesList.toString()}", )

//
        directoryAdaptor = DirectoriesAdopter { directoryModel, Int -> imageItemOnClick(directoryModel) }
        Log.e("TAG", "onCreate: The index coming is ${Int}", )
        val layoutManager = GridLayoutManager(this@ScanScreen, 3)
        binding.directoryID2.layoutManager = layoutManager
        binding.directoryID2.adapter = directoryAdaptor

        directoryAdaptor.submitList(imagesList.directories)
        directoryAdaptor.notifyDataSetChanged()


    }

    private fun imageItemOnClick(directoriesModel: DirectoriesModel) {
        Log.e("TAG", "imageItemOnClick: clicked image path is --> ${directoriesModel.image}", )
        val b = Bundle().apply {
            putSerializable(DIRECTORY_MODEL_ARGS, directoriesModel)
        }
        startActivityForResult(Intent(this, ImageView::class.java).putExtras(b),101)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 101) {
            val deletedIndex:Int = data?.extras?.getInt("Deleted_Index")?:-1
            if(deletedIndex > -1) {
                imagesList.directories.drop(deletedIndex)
                if(this@ScanScreen::directoryAdaptor.isInitialized) {
                    directoryAdaptor.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        val DIRECTORY_MODEL_ARGS = "DIRECTORY_MODEL_ARGS"
    }
}