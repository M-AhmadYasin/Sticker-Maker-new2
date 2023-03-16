package com.pro.stickermaker.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pro.stickermaker.R
import com.pro.stickermaker.whatsappcode.dataclasses.Sticker

class StickerAdapter(
    private val context: Context,
    private val list: List<Sticker> = listOf(),
    private val allowSelection: Boolean = false,
    private val allowLongClick: Boolean = true,
    private val on_sticker_click: (Sticker, longClick: Boolean, add: Boolean, remove: Boolean) -> Unit
) : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
    // selection impl

    private var selectionState = false
    private var availableHolder = mutableListOf<ViewHolder>()

    companion object {
        const val TAG = "StickerAdapter_Logs"
    }

    private var selectedHolder = mutableListOf<ViewHolder>()
    private var allSelected = false
    var selectAll: (select: Boolean) -> Unit = {
        Log.d(TAG, "invoking select all: $it")
        for (viewHolder in availableHolder) {
            if (it) {
                viewHolder.mask.visibility = View.VISIBLE
                viewHolder.checkBox.visibility = View.VISIBLE
                viewHolder.selected = true
                if (!selectedHolder.contains(viewHolder)) {
                    selectedHolder.add(viewHolder)
                }
                allSelected = true
                selectionState = true
            } else {
                viewHolder.mask.visibility = View.GONE
                viewHolder.checkBox.visibility = View.GONE
                viewHolder.selected = false
                selectedHolder.remove(viewHolder)
                allSelected = false
            }
        }
        selectionState = !selectedHolder.isEmpty()
    }
    var selectionStateInvoke: (boolean: Boolean) -> Unit = {
        Log.d(TAG, "selection invoked from inside adapter")
    }

    init {
        setHasStableIds(true)
    }

//    override fun getItemId(position: Int): Long {
//        return list[position].hashCode().toLong()
//    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    // selection impl


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sticker_image_view, parent, false)
        )
        if (!availableHolder.contains(holder)) {
            availableHolder.add(holder)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            if (position == 0 && !allowSelection) {
                Glide.with(context).load(R.drawable.add_more_placeholder)
                    .into(imageView)
            } else if (!allowLongClick && position == 0) {
                Glide.with(context).load(R.drawable.camera_icon).centerCrop()
                    .into(imageView)
                imageView.
                context.resources?.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp)?.let { imageView.setPadding(it) }
                imageView.setBackgroundColor(Color.parseColor("#20000000"))
            } else {
                Glide.with(context).load(R.drawable.page2).into(imageView)
            }

            itemView.setOnClickListener {
                when {
                    !allowSelection -> {
//                        on_sticker_click(list[position], false, false, false)
                        on_sticker_click(Sticker("", listOf()), false, false, false)
                    }
                    !selectionState -> {
//                        on_sticker_click(list[position], false, false, false)
                    }
                    position == 0 && !allowLongClick -> {
                        //fixme : capture new picture from camera
                    }
                    else -> {
                        if (!holder.selected) {
                            addHolderToSelectedList(holder, position)
                        } else {
                            removeHolderToSelectedList(holder, position)
                        }
                    }
                }
                selectionState = selectedHolder.isNotEmpty()
                if (!selectionState && !allowLongClick && position != 0) {
                    selectionStateInvoke.invoke(false)
                }
            }
            if (allowSelection && allowLongClick) {
                itemView.setOnLongClickListener {
                    if (!holder.selected) {
                        selectionState = true
                        addHolderToSelectedList(holder, position)
                    }
                    true
                }
            }
        }
        if (allSelected) {
            addHolderToSelectedList(holder, position)
        }
        if (setCheckBoxVisible) {
            selectionState = true
            if(position == 0 && !allowLongClick) return
            holder.checkBox.visibility = View.VISIBLE
        } else {
            holder.checkBox.visibility = View.GONE
            selectionState = false
        }
    }

    private fun addHolderToSelectedList(holder: ViewHolder, position: Int) {
        Log.d(TAG, "addHolderToSelectedList: selecting holder")
        holder.mask.visibility = View.VISIBLE
        holder.checkBox.visibility = View.VISIBLE
        holder.selected = true
//        on_sticker_click(list[position], true, true, false)
        if (!selectedHolder.contains(holder)) {
            selectedHolder.add(holder)
        }

    }

    private fun removeHolderToSelectedList(holder: ViewHolder, position: Int) {
        Log.d(TAG, "removeHolderToSelectedList: unselecting holder")
        holder.mask.visibility = View.GONE
        holder.checkBox.visibility = View.GONE
        holder.selected = false
//        on_sticker_click(list[position], true, false, true)
        selectedHolder.remove(holder)
    }

    private var setCheckBoxVisible = false
    fun enableSelection(boolean: Boolean) {
        setCheckBoxVisible = boolean
        if (!boolean) {
            selectedHolder.clear()
            availableHolder.forEach {
                it.checkBox.visibility = View.GONE
                it.mask.visibility = View.GONE
                it.selected = false
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imageView = v.findViewById<ImageView>(R.id.item_sticker_image_view)
        val checkBox = v.findViewById<ImageView>(R.id.item_sticker_image_check_box)
        val mask: ImageView = v.findViewById(R.id.listItem_mask)
        var selected = false
    }
}