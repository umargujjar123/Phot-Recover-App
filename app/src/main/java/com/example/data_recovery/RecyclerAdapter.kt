package com.example.data_recovery

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var title = arrayOf("chapter1, chaperter2, chaperter3,chaperter4,chaperter5,chaperter6,chaperter7,chaperter8,chaperter9,chaperter10,")
    private val image = intArrayOf(R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet,R.drawable.datesheet)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_row, parent, false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
       holder.textID.text = title[position]
        holder.imageID.setImageResource(image[position])
    }

    override fun getItemCount(): Int {
        return title.size
    }
inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    var imageID: ImageView
    var textID: TextView
    init {
        imageID  = itemView.findViewById(R.id.image_ID)
        textID = itemView.findViewById((R.id.text_ID))
    }
}

}