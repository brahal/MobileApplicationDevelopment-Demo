package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import androidx.room.TypeConverter

class RoomConverters {
    @TypeConverter
    fun fromImageStorage(v: ImageStorage?): String = (v ?: ImageStorage.LOCAL).name

    @TypeConverter
    fun toImageStorage(v: String?): ImageStorage =
        runCatching { ImageStorage.valueOf(v ?: "LOCAL") }.getOrDefault(ImageStorage.LOCAL)
}