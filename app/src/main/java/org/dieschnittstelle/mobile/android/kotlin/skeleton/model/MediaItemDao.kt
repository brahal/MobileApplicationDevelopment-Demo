package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MediaItemDao {

    @Query("SELECT * FROM MediaItem")
    fun alleAuslesen(): List<MediaItem>

    @Insert
    fun erstellen(item: MediaItem): Long

    @Delete
    fun loeschen(item: MediaItem)

    @Update
    fun aktualisieren(item: MediaItem)
}