import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem

@Composable
fun ListView(mediaItems: MutableList<MediaItem>, scrollToEnd: MutableState<Boolean>, onSelect:(MediaItem, (Boolean)->Unit)->Unit, onOptions:(MediaItem)->Unit, modifier: Modifier = Modifier) {
    Log.i("ListView", "re(composing)")

    val scrollStateOfList = rememberLazyListState()

    LaunchedEffect(mediaItems.size) {
        if (scrollToEnd.value) {
            if (!mediaItems.isEmpty()) {
                scrollStateOfList.animateScrollToItem(mediaItems.size -1)
                scrollToEnd.value = false
            }
        }
    }

    LazyColumn(state = scrollStateOfList, modifier = modifier) {
        Log.i("LazyColumn List", "re(composing)")
        items(mediaItems) {mediaItem ->
            Log.i("ListView", "handling: ${mediaItem.title}")
            ListItemView(mediaItem,
                onSelect = onSelect,
                onOptions = onOptions,
                modifier = Modifier)
            HorizontalDivider(
                thickness = 2.dp,
                color = Color(0xFF2C2C2C)
            )
        }
    }
}
