package com.example.data_recovery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.data_recovery.ScanScreen.Companion.DIRECTORY_MODEL_ARGS
import com.example.data_recovery.databinding.ImageViewBinding
import com.example.data_recovery.model.DirectoriesModel
import java.io.*
import java.nio.channels.FileChannel

class ImageView : AppCompatActivity() {

    lateinit var binding: ImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.image_view)

        val imageData1: DirectoriesModel = intent.getSerializableExtra(DIRECTORY_MODEL_ARGS) as DirectoriesModel
        Log.e("TAG", "onCreate: Image that gets is $imageData1" )

        imageData1.image?.let {
            binding.image.setImageURI(Uri.fromFile(File(it)))
        }
        binding.deleteBtn.setOnClickListener(){
            imageData1.image?.let {
                File(it).delete()
            }
        }
        binding.recoverBtn.setOnClickListener(){


            imageData1.image?.let{
           copyFile(File(it),  File(Environment.getExternalStorageDirectory()
               .getAbsolutePath() + "/MyFolder/"))
                Log.e(
                    "TAG",
                    "onCreate: The new folder created is ${File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/Recovered Images/")}"
                )
                Log.e("TAG", "onCreate: The path of the source file is ${File(it)}")
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        setResult(101, Intent().putExtra("Deleted_Index",2))
        finish()
    }





    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).getChannel()
            destination = FileOutputStream(destFile).getChannel()
            destination.transferFrom(source, 0, source.size())
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
    }



}