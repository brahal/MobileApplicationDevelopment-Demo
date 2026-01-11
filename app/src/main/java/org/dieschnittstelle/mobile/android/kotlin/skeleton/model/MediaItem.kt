package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlin.random.Random

// var itemCount = 0L

enum class ImageStorage { LOCAL, REMOTE }

@Entity
@Serializable
class MediaItem(
    var title: String = "direm",
    var src: String = "https://picsum.photos/75/75",
    var imageStorage: ImageStorage = ImageStorage.LOCAL
) {
    var createdOrModified = System.currentTimeMillis()

    @PrimaryKey(autoGenerate = true)
    var id = 0L

}

// node webserver.js

fun createRandomMediaItem(): MediaItem {
    val imgs = listOf(
        "https://picsum.photos/40/50",
        "https://picsum.photos/50/50",
        "https://picsum.photos/75/75",
        "https://picsum.photos/100/50"
    )
    val titles = listOf("lorem", "ipsum", "dolor", "sed", "direm")

    return MediaItem(
        titles.get(Random.nextInt(0, titles.size)),
        imgs.get(Random.nextInt(0, imgs.size)),
        imageStorage = ImageStorage.REMOTE
    )
}