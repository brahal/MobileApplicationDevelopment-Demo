package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlin.random.Random

enum class ImageStorage { LOCAL, REMOTE }

@Entity
@Serializable
class MediaItem(
    var title: String = "direm",
    var src: String = "https://picsum.photos/75/75",
    var imageStorage: ImageStorage = ImageStorage.LOCAL,
    var latitude: Double? = null,
    var longitude: Double? = null

) {
    var createdOrModified = System.currentTimeMillis()

    //id = Primärschlüssel
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}

//Liefert einen zufälligen Default-Standort (Berlin)
fun randomDefaultLocation(): Pair<Double, Double> {
    val baseLat = 52.5200
    val baseLng = 13.4050

    val randomLat = baseLat + Random.nextDouble(-0.05, 0.05)
    val randomLng = baseLng + Random.nextDouble(-0.05, 0.05)

    return randomLat to randomLng
}

fun createRandomMediaItem(): MediaItem {
    val imgs = listOf(
        "https://picsum.photos/40/50",
        "https://picsum.photos/50/50",
        "https://picsum.photos/75/75",
        "https://picsum.photos/100/50"
    )

    val titles = listOf("lorem", "ipsum", "dolor", "sed", "direm")

    val (lat, lng) = randomDefaultLocation()

    return MediaItem(
        titles.get(Random.nextInt(0, titles.size)),
        imgs.get(Random.nextInt(0, imgs.size)),
        imageStorage = ImageStorage.REMOTE,
        latitude = lat,
        longitude = lng
    )
}
