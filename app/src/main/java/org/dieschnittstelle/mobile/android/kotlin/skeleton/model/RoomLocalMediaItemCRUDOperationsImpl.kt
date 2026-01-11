package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomLocalMediaItemCRUDOperationsImpl(
    context: Context,
    private var items: MutableList<MediaItem>
) : IMediaItemCRUDOperations {

    // 1 -> 2: imageStorage hinzufügen (TEXT, default LOCAL)
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE MediaItem ADD COLUMN imageStorage TEXT NOT NULL DEFAULT 'LOCAL'")
        }
    }

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

    @Database(entities = [MediaItem::class], version = 2)
    @TypeConverters(RoomConverters::class)
    abstract class MediaItemDatabase : RoomDatabase() {
        abstract fun mediaItemDao(): MediaItemDao
    }

    private val db = Room.databaseBuilder(
        context,
        MediaItemDatabase::class.java,
        "mediaItem-db"
    )
        .addMigrations(MIGRATION_1_2)
        .build()

    private val myDao: MediaItemDao = db.mediaItemDao()

    override fun createItem(item: MediaItem): MediaItem {
        val idOfCreatedItem = myDao.erstellen(item)
        item.id = idOfCreatedItem
        items.add(item)
        return item
    }

    override fun readAllItems(): List<MediaItem> {
        items.clear() // Duplicate vermeiden
        // Thread.sleep(3000)  // ⚠️ lieber entfernen, bremst nur
        items.addAll(myDao.alleAuslesen())
        return items
    }

    override fun readItem(id: Long): MediaItem {
        return items.find { it.id == id }!!
    }

    override fun updateItem(id: Long, item: MediaItem): MediaItem {
        val itemInList = readItem(item.id)
        val indexOfItem = items.indexOf(itemInList)

        // ✅ WICHTIG: imageStorage + src + title persistieren
        val itemCopy = MediaItem(
            title = item.title,
            src = item.src,
            imageStorage = item.imageStorage
        ).apply {
            this.id = item.id
            this.createdOrModified = item.createdOrModified
        }

        myDao.aktualisieren(itemCopy)

        items.remove(itemInList)
        items.add(indexOfItem, itemCopy)
        return itemCopy
    }

    override fun deleteItem(id: Long): Boolean {
        val itemToBeDeleted = readItem(id)
        // Thread.sleep(1500)  // ⚠️ lieber entfernen
        myDao.loeschen(itemToBeDeleted)
        items.remove(itemToBeDeleted)
        return true
    }
}