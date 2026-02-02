import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.ImageStorage
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.utils.DateUtils

@Composable
fun ListItemView(
    myItem: MediaItem,
    onSelect: (MediaItem, (Boolean) -> Unit) -> Unit,
    onOptions: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {

    val createdOrUpdatedState = mutableStateOf(myItem.createdOrModified)

    Row(
        modifier = Modifier
            .clickable(onClick = {
                onSelect.invoke(myItem) { updated ->
                    if (updated) {
                        createdOrUpdatedState.value = myItem.createdOrModified
                    }
                }
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = myItem.src,
            contentDescription = myItem.title,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .padding(8.dp)
                .height(60.dp)
                .width(60.dp)
        )
        Column(modifier = modifier.weight(1f)) {
            Text(myItem.title, fontSize = 30.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

            // FRM2 Anforderung 5
            val badgeText = if (myItem.imageStorage == ImageStorage.REMOTE) "REMOTE" else "LOCAL"
            val badgeColor =
                if (myItem.imageStorage == ImageStorage.REMOTE) Color(0xFF1DB954) else Color(
                    0xFF9E9E9E
                )
            // Local or Remote anzeige
            Text(
                text = badgeText,
                fontSize = 10.sp,
                color = badgeColor,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(DateUtils.formatDate(myItem.createdOrModified), fontSize = 18.sp)
            Text(
                createdOrUpdatedState.value.toString(),
                fontSize = 0.sp,
                modifier = modifier.height(0.dp)
            )
        }

        IconButton(onClick = {
            onOptions.invoke(myItem)
        }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert, contentDescription = "Options",
                tint = Color.White,
                modifier = modifier.size(40.dp)
            )
        }
    }
}

