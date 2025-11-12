import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem

@Composable
fun ListView(mediaItems: MutableList<MediaItem>, modifier: Modifier = Modifier) {
    Log.i("ListView", "re(composing)")
    LazyColumn {
        Log.i("LazyColumn List", "re(composing)")
        items(mediaItems) {mediaItem ->
            Log.i("ListView", "handling: ${mediaItem.title}")
            ListItemView(mediaItem,
                onSelect = {item, ondoneCallback ->
                    item.title += (" " + item.title)
                    item.createdOrModified = System.currentTimeMillis()
                    ondoneCallback.invoke(true)
                },
                onOptions = {
                    mediaItems.remove(mediaItem)
                },
                modifier = modifier)
        }
    }
}
