package com.pro.stickermaker.whatsappcode.dataclasses

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator


//@kotlinx.serialization.Serializable
@Suppress("PropertyName")
open class StickerPack : Parcelable {
    var identifier: String?
    var name: String?
    var publisher: String?
    var tray_image_file: String?
    val publisher_email: String?
    val publisher_website: String?
    val privacy_policy_website: String?
    val license_agreement_website: String?
    /**
     * Update version when adding stickers to an existing pack
     * */
    var image_data_version: String? = null
    var avoid_cache: Boolean = false
    var animated_sticker_pack = false
    var stickers: List<Sticker>? = null
    private var total_size: Long = 0
    var ios_app_store_link: String? = ""
    var android_play_store_link: String? = ""
    private var is_whitelisted = false

    constructor(
        identifier: String?,
        name: String?,
        publisher: String?,
        trayImageFile: String?,
        publisherEmail: String?,
        publisherWebsite: String?,
        privacyPolicyWebsite: String?,
        licenseAgreementWebsite: String?,
        imageDataVersion: String?,
        avoidCache: Boolean,
        animatedStickerPack: Boolean
    ) {
        this.identifier = identifier
        this.name = name
        this.publisher = publisher
        this.tray_image_file = trayImageFile
        this.publisher_email = publisherEmail
        this.publisher_website = publisherWebsite
        this.privacy_policy_website = privacyPolicyWebsite
        this.license_agreement_website = licenseAgreementWebsite
        this.image_data_version = imageDataVersion
        this.avoid_cache = avoidCache
        this.animated_sticker_pack = animatedStickerPack
    }

    protected constructor(`in`: Parcel) {
        identifier = `in`.readString()
        name = `in`.readString()
        publisher = `in`.readString()
        tray_image_file = `in`.readString()
        publisher_email = `in`.readString()
        publisher_website = `in`.readString()
        privacy_policy_website = `in`.readString()
        license_agreement_website = `in`.readString()
        ios_app_store_link = `in`.readString()
        stickers = `in`.createTypedArrayList(Sticker.CREATOR)
        total_size = `in`.readLong()
        android_play_store_link = `in`.readString()
        is_whitelisted = `in`.readByte().toInt() != 0
        image_data_version = `in`.readString()
        avoid_cache = `in`.readByte().toInt() != 0
        animated_sticker_pack = `in`.readByte().toInt() != 0
    }

    fun set_Stickers(stickers: List<Sticker>) {
        this.stickers = stickers
        total_size = 0
        for (sticker in stickers) {
            total_size += sticker.size
        }
    }

//    fun setAndroidPlayStoreLink(androidPlayStoreLink: String?) {
//        this.androidPlayStoreLink = androidPlayStoreLink
//    }
//
//    fun setIosAppStoreLink(iosAppStoreLink: String?) {
//        this.iosAppStoreLink = iosAppStoreLink
//    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(identifier)
        dest.writeString(name)
        dest.writeString(publisher)
        dest.writeString(tray_image_file)
        dest.writeString(publisher_email)
        dest.writeString(publisher_website)
        dest.writeString(privacy_policy_website)
        dest.writeString(license_agreement_website)
        dest.writeString(ios_app_store_link)
        dest.writeTypedList(stickers)
        dest.writeLong(total_size)
        dest.writeString(android_play_store_link)
        dest.writeByte((if (is_whitelisted) 1 else 0).toByte())
        dest.writeString(image_data_version)
        dest.writeByte((if (avoid_cache) 1 else 0).toByte())
        dest.writeByte((if (animated_sticker_pack) 1 else 0).toByte())
    }


    fun getParceledPack(): Parcel {
        val parcel = Parcel.obtain()
        parcel.recycle()
        parcel.writeString(identifier)
        parcel.writeString(name)
        parcel.writeString(publisher)
        parcel.writeString(tray_image_file)
        parcel.writeString(publisher_email)
        parcel.writeString(publisher_website)
        parcel.writeString(privacy_policy_website)
        parcel.writeString(license_agreement_website)
        parcel.writeString(ios_app_store_link)
        parcel.writeTypedList(stickers)
        parcel.writeLong(total_size)
        parcel.writeString(android_play_store_link)
        parcel.writeByte((if (is_whitelisted) 1 else 0).toByte())
        parcel.writeString(image_data_version)
        parcel.writeByte((if (avoid_cache) 1 else 0).toByte())
        parcel.writeByte((if (animated_sticker_pack) 1 else 0).toByte())
        return parcel
    }

    companion object {
        @JvmField
        val CREATOR: Creator<StickerPack> = object : Creator<StickerPack> {
            override fun createFromParcel(`in`: Parcel): StickerPack {
                return StickerPack(`in`)
            }

            override fun newArray(size: Int): Array<StickerPack?> {
                return arrayOfNulls(size)
            }
        }
    }
}