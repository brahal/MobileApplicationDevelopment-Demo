package org.dieschnittstelle.mobile.android.kotlin.skeleton.model

import kotlin.random.Random

var itemCount = 0
class MediaItem (var title: String="direm", var src:String = "https://picsum.photos/75/75") {
    var createdOrModified = System.currentTimeMillis()
    var myCount = itemCount++
}

fun createRandomMediaItem() : MediaItem {
    val imgs = listOf("https://picsum.photos/40/50", "https://picsum.photos/50/50", "https://picsum.photos/75/75", "https://picsum.photos/100/50")
    val titles = listOf("lorem", "ipsum","dolor", "sed", "direm")

    return MediaItem(titles.get(Random.nextInt(0,titles.size)), imgs.get(Random.nextInt(0, imgs.size)))
}