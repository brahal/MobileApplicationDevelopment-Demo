package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

interface IMediaItemCRUDOperations {
    fun createItem(item: MediaItem): MediaItem

    fun readAllItems(): List<MediaItem>

    fun readItem(id: Long): MediaItem

    fun updateItem(id: Long, item: MediaItem): MediaItem

    fun deleteItem(id: Long): Boolean
}