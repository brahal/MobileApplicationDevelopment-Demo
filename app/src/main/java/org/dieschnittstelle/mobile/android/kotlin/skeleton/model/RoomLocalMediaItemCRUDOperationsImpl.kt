package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

class RoomLocalMediaItemCRUDOperationsImpl(context: Context, private var items: MutableList<MediaItem>) : IMediaItemCRUDOperations {

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

    @Database(entities = [MediaItem::class], version = 1)
    abstract class MediaItemDatabase : RoomDatabase() {

        abstract fun mediaItemDao(): MediaItemDao
    }

    val db = Room.databaseBuilder(context, MediaItemDatabase::class.java, "mediaItem-db" ).build()
    val myDao: MediaItemDao = db.mediaItemDao()

    override fun createItem(item: MediaItem): MediaItem {
         val idOfCreatedItem = myDao.erstellen(item)
        item.id = idOfCreatedItem
        items.add(item)
        return item
    }

    override fun readAllItems(): List<MediaItem> {
        Thread.sleep(3000)
        items.addAll(myDao.alleAuslesen())
        return items

    }

    override fun readItem(id: Long): MediaItem {
      return items.find { it.id == id }!!
    }

    override fun updateItem(id: Long, item: MediaItem): MediaItem {
          myDao.aktualisieren(item)
        return item
    }

    override fun deleteItem(id: Long): Boolean {
        val itemToBeDeleted = this.readItem(id)
        Thread.sleep(1500)
        myDao.loeschen(itemToBeDeleted)
        items.remove(itemToBeDeleted)
        return true
    }
}