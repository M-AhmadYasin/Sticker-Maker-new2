package com.pro.stickermaker.whatsappcode.dataclasses

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

//@kotlinx.serialization.Serializable
class Sticker : Parcelable {
    var image_file: String?
    var emojis: List<String>?
    var size: Long = 0

    constructor(imageFileName: String?, emojis: List<String>?) {
        this.image_file = imageFileName
        this.emojis = emojis
    }

    constructor(`in`: Parcel) {
        image_file = `in`.readString()
        emojis = `in`.createStringArrayList()
        size = `in`.readLong()
    }


    fun set_Size(size: Long) {
        this.size = size
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(image_file)
        dest.writeStringList(emojis)
        dest.writeLong(size)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<Sticker> = object : Creator<Sticker> {
            override fun createFromParcel(`in`: Parcel): Sticker {
                return Sticker(`in`)
            }

            override fun newArray(size: Int): Array<Sticker?> {
                return arrayOfNulls(size)
            }
        }
    }
}