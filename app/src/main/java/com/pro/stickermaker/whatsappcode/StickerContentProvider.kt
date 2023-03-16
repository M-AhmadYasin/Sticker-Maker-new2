/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.pro.stickermakerimpl.whatsappcode

import android.content.*
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.util.Log
import com.pro.stickermaker.BuildConfig
import com.pro.stickermaker.whatsappcode.ContentFileParser
import com.pro.stickermaker.whatsappcode.Main_STICKERS_Shared_Pref_Key
import com.pro.stickermaker.whatsappcode.STICKERS_Shared_Pref_Name
import com.pro.stickermaker.whatsappcode.dataclasses.StickerPack
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.util.*

class StickerContentProvider : ContentProvider() {
    var stickerPackList: List<StickerPack>? = null
    private var _context: Context? = null
    private val ctx get() = _context!!

    init {
        context?.let { _context = it }
        INSTANCE = this
    }

    override fun onCreate(): Boolean {

        context?.let { _context = it }
        val authority = BuildConfig.CONTENT_PROVIDER_AUTHORITY
        check(authority.startsWith(ctx.packageName)) { "your authority (" + authority + ") for the content provider should start with your package name: " + ctx.packageName }

        //the call to get the metadata for the sticker packs.
        MATCHER.addURI(authority, METADATA, METADATA_CODE)

        //the call to get the metadata for single sticker pack. * represent the identifier
        MATCHER.addURI(authority, "$METADATA/*", METADATA_CODE_FOR_SINGLE_PACK)

        //gets the list of stickers for a sticker pack, * respresent the identifier.
        MATCHER.addURI(authority, "$STICKERS/*", STICKERS_CODE)
        for (stickerPack in get_StickerPackList() ?: listOf()) {
            MATCHER.addURI(
                authority,
                STICKERS_ASSET + "/" + stickerPack.identifier + "/" + stickerPack.tray_image_file,
                STICKER_PACK_TRAY_ICON_CODE
            )
            for (sticker in stickerPack.stickers ?: listOf()) {
                MATCHER.addURI(
                    authority,
                    STICKERS_ASSET + "/" + stickerPack.identifier + "/" + sticker.image_file,
                    STICKERS_ASSET_CODE
                )
            }
        }
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        Log.d(TAG, "query: --------------------------------------- ${MATCHER.match(uri)}")
        return when (MATCHER.match(uri)) {
            METADATA_CODE -> {
                getPackForAllStickerPacks(uri)
            }
            METADATA_CODE_FOR_SINGLE_PACK -> {
                getCursorForSingleStickerPack(uri)
            }
            STICKERS_CODE -> {
                getStickersForAStickerPack(uri)
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        val matchCode = MATCHER.match(uri)
        Log.d(TAG, "openAssetFile: mode for reading  $matchCode")
        return if (matchCode == STICKERS_ASSET_CODE || matchCode == STICKER_PACK_TRAY_ICON_CODE) {
            getImageAsset(uri)
        } else null
    }

    override fun getType(uri: Uri): String {
        return when (MATCHER.match(uri)) {
            METADATA_CODE -> "vnd.android.cursor.dir/vnd." + BuildConfig.CONTENT_PROVIDER_AUTHORITY + "." + METADATA
            METADATA_CODE_FOR_SINGLE_PACK -> "vnd.android.cursor.item/vnd." + BuildConfig.CONTENT_PROVIDER_AUTHORITY + "." + METADATA
            STICKERS_CODE -> "vnd.android.cursor.dir/vnd." + BuildConfig.CONTENT_PROVIDER_AUTHORITY + "." + STICKERS
            STICKERS_ASSET_CODE -> "image/webp"
            STICKER_PACK_TRAY_ICON_CODE -> "image/png"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    @Synchronized
    private fun readContentFile(context: Context) {
        try {
            val pref = context.getSharedPreferences(STICKERS_Shared_Pref_Name, Context.MODE_PRIVATE)
            val string = pref.getString(Main_STICKERS_Shared_Pref_Key, "")
                .toString()/*.removePrefix("[").dropLast(1)*/
            Log.i(TAG, "byte array = $string")
            val inputStream = ByteArrayInputStream(string.toByteArray())
//            context.assets.open(CONTENT_FILE_NAME).use { contentsInputStream ->
            inputStream.use { contentsInputStream ->
                stickerPackList =
                    ContentFileParser.setContext(context).parseStickerPacks(contentsInputStream)
                Log.d(TAG, "after parsing sizeOfList = ${stickerPackList?.size}")
                Log.d(TAG, stickerPackList?.toString() ?: "")
            }
        } catch (e: IOException) {
            throw RuntimeException(CONTENT_FILE_NAME + " file has some issues: " + e.message, e)
        } catch (e: IllegalStateException) {
            throw RuntimeException(CONTENT_FILE_NAME + " file has some issues: " + e.message, e)
        }
    }

    private fun get_StickerPackList(): List<StickerPack>? {
        if (stickerPackList == null) {
        readContentFile(ctx)
        }
        return stickerPackList
    }

    private fun getPackForAllStickerPacks(uri: Uri): Cursor {
        return getStickerPackInfo(uri, get_StickerPackList()!!)
    }

    private fun getCursorForSingleStickerPack(uri: Uri): Cursor {
        val identifier = uri.lastPathSegment
        Log.d(TAG, "getCursorForSingleStickerPack: identifier = $identifier")
        for (stickerPack in get_StickerPackList()!!) {
            if (identifier == stickerPack.identifier) {
                return getStickerPackInfo(uri, listOf(stickerPack))
            }
        }
        return getStickerPackInfo(uri, ArrayList())
    }

    private fun getStickerPackInfo(uri: Uri, stickerPackList: List<StickerPack>): Cursor {
        val cursor = MatrixCursor(
            arrayOf(
                STICKER_PACK_IDENTIFIER_IN_QUERY,
                STICKER_PACK_NAME_IN_QUERY,
                STICKER_PACK_PUBLISHER_IN_QUERY,
                STICKER_PACK_ICON_IN_QUERY,
                ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
                IOS_APP_DOWNLOAD_LINK_IN_QUERY,
                PUBLISHER_EMAIL,
                PUBLISHER_WEBSITE,
                PRIVACY_POLICY_WEBSITE,
                LICENSE_AGREEMENT_WEBSITE,
                IMAGE_DATA_VERSION,
                AVOID_CACHE,
                ANIMATED_STICKER_PACK
            )
        )
        Log.d(TAG, "getStickerPackInfo: sticker details = ${stickerPackList.size}")
        for (stickerPack in stickerPackList) {
            val builder = cursor.newRow()
            builder.add(stickerPack.identifier)
            builder.add(stickerPack.name)
            builder.add(stickerPack.publisher)
            builder.add(stickerPack.tray_image_file)
            builder.add(stickerPack.android_play_store_link)
            builder.add(stickerPack.ios_app_store_link)
            builder.add(stickerPack.publisher_email)
            builder.add(stickerPack.publisher_website)
            builder.add(stickerPack.privacy_policy_website)
            builder.add(stickerPack.license_agreement_website)
            builder.add(stickerPack.image_data_version)
            builder.add(if (stickerPack.avoid_cache) 1 else 0)
            builder.add(if (stickerPack.animated_sticker_pack) 1 else 0)
        }
        cursor.setNotificationUri(ctx.contentResolver, uri)
        return cursor
    }

    private fun getStickersForAStickerPack(uri: Uri): Cursor {
        val identifier = uri.lastPathSegment
        Log.d(TAG, "getStickersForAStickerPack: identifier $identifier")
        val cursor = MatrixCursor(arrayOf(STICKER_FILE_NAME_IN_QUERY, STICKER_FILE_EMOJI_IN_QUERY))
        for (stickerPack in get_StickerPackList()!!) {
            if (identifier == stickerPack.identifier) {
                for (sticker in stickerPack.stickers!!) {
                    Log.d(
                        TAG,
                        "getStickersForAStickerPack: adding image file  ${sticker.image_file}"
                    )
                    cursor.addRow(
                        arrayOf<Any?>(
                            sticker.image_file,
                            TextUtils.join(",", sticker.emojis!!)
                        )
                    )
                }
            }
        }
        cursor.setNotificationUri(ctx.contentResolver, uri)
        return cursor
    }

    @Throws(IllegalArgumentException::class)
    private fun getImageAsset(uri: Uri): AssetFileDescriptor? {
        Log.d(TAG, "getImageAsset: uri --> $uri")
        val am = ctx.assets
        val pathSegments = uri.pathSegments
        require(pathSegments.size == 3) { "path segments should be 3, uri is: $uri" }
        val fileName = pathSegments[pathSegments.size - 1]
        val identifier = pathSegments[pathSegments.size - 2]
        require(!TextUtils.isEmpty(identifier)) { "identifier is empty, uri: $uri" }
        require(!TextUtils.isEmpty(fileName)) { "file name is empty, uri: $uri" }
        //making sure the file that is trying to be fetched is in the list of stickers.
        for (stickerPack in get_StickerPackList()!!) {
            if (identifier == stickerPack.identifier) {
                if (fileName == stickerPack.tray_image_file) {
                    return fetchFile(uri, am, fileName, identifier)
                } else {
                    for (sticker in stickerPack.stickers!!) {
                        if (fileName == sticker.image_file) {
                            return fetchFile(uri, am, fileName, identifier)
                        }
                    }
                }
            }
        }
        return null
    }

    private fun fetchFile(
        uri: Uri,
        am: AssetManager,
        fileName: String,
        identifier: String
    ): AssetFileDescriptor? {
        var path = uri.path ?: ""
        path = path.replace("stickers_asset/", "", true)
        val parent = ctx.getDir(identifier, Context.MODE_PRIVATE)
        return try {
            val file = File(parent, fileName)
            val parcelFileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            AssetFileDescriptor(parcelFileDescriptor, 0, file.length())

//            am.openFd("$identifier/$fileName")
        } catch (e: IOException) {
            Log.e(
                ctx.packageName,
                "IOException when getting asset file, ${parent.path} $fileName",
                e
            )
            null
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Not supported")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Not supported")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not supported")
    }

    companion object {
        /**
         * Do not change the strings listed below, as these are used by WhatsApp. And changing these will break the interface between sticker app and WhatsApp.
         */
        const val STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier"
        const val STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name"
        const val STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher"
        const val STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon"
        const val ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link"
        const val IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link"
        const val PUBLISHER_EMAIL = "sticker_pack_publisher_email"
        const val PUBLISHER_WEBSITE = "sticker_pack_publisher_website"
        const val PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website"
        const val LICENSE_AGREEMENT_WEBSITE = "sticker_pack_license_agreement_website"
        const val IMAGE_DATA_VERSION = "image_data_version"
        const val AVOID_CACHE = "whatsapp_will_not_cache_stickers"
        const val ANIMATED_STICKER_PACK = "animated_sticker_pack"
        const val STICKER_FILE_NAME_IN_QUERY = "sticker_file_name"
        const val STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji"
        private const val CONTENT_FILE_NAME = "contents.json"

        /**
         * Do not change the values in the UriMatcher because otherwise, WhatsApp will not be able to fetch the stickers from the ContentProvider.
         */
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)
        private const val METADATA = "metadata"
        private const val METADATA_CODE = 1
        private const val METADATA_CODE_FOR_SINGLE_PACK = 2
        const val STICKERS = "stickers"
        private const val STICKERS_CODE = 3
        const val STICKERS_ASSET = "stickers_asset"
        private const val STICKERS_ASSET_CODE = 4
        private const val STICKER_PACK_TRAY_ICON_CODE = 5
        val AUTHORITY_URI: Uri = Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(
            BuildConfig.CONTENT_PROVIDER_AUTHORITY
        ).appendPath(METADATA).build()

        private const val TAG = "Content_Provider_Logs"


        private var INSTANCE: StickerContentProvider? = null
        fun getInstance(): StickerContentProvider {
            return INSTANCE ?: StickerContentProvider()
        }
    }
}