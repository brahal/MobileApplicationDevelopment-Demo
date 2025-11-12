package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

class SimpleMediaItemCRUDOperationsImpl(private var items:MutableList<MediaItem>) : IMediaItemCRUDOperations {


    override fun createItem(item: MediaItem): MediaItem {
        items.add(item)
        return item
    }

    override fun readAllItems(): List<MediaItem> {
        if (items.size == 0) {
            for ( i  in 0 .. 5 ) {
                items.add(createRandomMediaItem())
            }
        }
        return items
    }

    override fun readItem(id: Long): MediaItem {
       return items.find {it.myCount.toLong() == id}!!
    }

    override fun updateItem(id: Long, item: MediaItem): MediaItem {
        return  item
    }

    override fun deleteItem(id: Long): Boolean {
        items.remove(readItem(id))
        return true
    }
}