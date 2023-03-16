package com.pro.stickermaker.sahredpref

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.pro.stickermaker.whatsappcode.Main_STICKERS_Shared_Pref_Key
import com.pro.stickermaker.whatsappcode.STICKERS_Shared_Pref_Name
import com.pro.stickermaker.whatsappcode.dataclasses.StickerPack
import java.lang.reflect.Type

object SharedPrefs {
    private const val TAG = "SharedPrefs_ObjectTag"
    fun writeStickerJSONToPrefs(sb: List<StickerPack?>?, context: Context): Boolean {
        return try {
            val mSettings = context.getSharedPreferences(STICKERS_Shared_Pref_Name, Context.MODE_PRIVATE)
            val writeValue: String =/*"{\"sticker_packs\":"+ */GsonBuilder()
                .registerTypeAdapter(
                    Uri::class.java,
                    UriSerializer()
                )
                .create()
                .toJson(
                    sb,
                    object : TypeToken<ArrayList<StickerPack?>?>() {}.type
                )/*+"}"*/
            Log.d(TAG, "writing JSON as $writeValue ")
            val mEditor = mSettings.edit()
            mEditor.putString(Main_STICKERS_Shared_Pref_Key, writeValue)
            mEditor.apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun readStickerJSONFromPrefs(context: Context): ArrayList<StickerPack?>? {
        val mSettings = context.getSharedPreferences(STICKERS_Shared_Pref_Name, Context.MODE_PRIVATE)
        val loadValue = mSettings.getString(Main_STICKERS_Shared_Pref_Key, "")
        val listType: Type = object : TypeToken<ArrayList<StickerPack?>?>() {}.type
        return GsonBuilder()
            .registerTypeAdapter(
                Uri::class.java,
                UriDeserializer()
            )
            .create()
            .fromJson(loadValue, listType)
    }

    class UriDeserializer : JsonDeserializer<Uri> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            src: JsonElement, srcType: Type,
            context: JsonDeserializationContext
        ): Uri {
            return Uri.parse(src.toString().replace("\"", ""))
        }
    }

    class UriSerializer : JsonSerializer<Uri> {
        override fun serialize(
            src: Uri,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(src.toString())
        }
    }
}