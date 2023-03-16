package com.pro.stickermaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pro.stickermaker.R

class StickerPackAdapter(private val context: Context, private val onClick: (uniqueIdentifier: String) -> Unit) : RecyclerView.Adapter<StickerPackAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sticker_pack, parent, false))
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            stickerName.text = stickerName.text.toString()+(position+1).toString()
//            recyclerView.adapter = StickerAdapter(context){stickerIndex->
////                onClick Callback
//                onClick("uniqueIdentifier")
//            }
            firstImage.setImageDrawable(context.resources.getDrawable(R.drawable.page2,context.theme))
            secondImage.setImageDrawable(context.resources.getDrawable(R.drawable.page2,context.theme))
            thirdImage.setImageDrawable(context.resources.getDrawable(R.drawable.page2,context.theme))
            forthImage.setImageDrawable(context.resources.getDrawable(R.drawable.page2,context.theme))
            itemView.setOnClickListener {
                onClick("uniqueIdentifier")
            }
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val stickerName = v.findViewById<TextView>(R.id.item_sticker_pack_name)
        val packSize = v.findViewById<TextView>(R.id.item_sticker_pack_size)
        val firstImage = v.findViewById<ImageView>(R.id.first_imageView)
        val secondImage = v.findViewById<ImageView>(R.id.second_imageView)
        val thirdImage = v.findViewById<ImageView>(R.id.third_imageView)
        val forthImage = v.findViewById<ImageView>(R.id.forth_imageView)
        val addButton = v.findViewById<MaterialButton>(R.id.item_sticker_pack_add_button)

//        val recyclerView = v.findViewById<RecyclerView>(R.id.item_sticker_pack_rv)
    }
}