package org.dieschnittstelle.mobile.android.kotlin.skeleton

import androidx.compose.foundation.layout.*
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.dieschnittstelle.mobile.android.kotlin.skeleton.ui.theme.MADDemoTheme
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()

        val usename = mutableStateOf("MAD")

        val mediaItems = mutableStateListOf<MediaItem>()
        for ( i  in 0 .. 5 ) {
            mediaItems.add(createRandomMediaItem())
        }

        setContent {
            MADDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton({
                            Log.i("Add new Item", "onclick()" )
                            mediaItems.add(createRandomMediaItem())
                            Log.i("Add new Item", "added new Item to List" )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                ) { innerPadding ->
                    ListView(mediaItems, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(usename: String, modifier: Modifier = Modifier) {
    Log.i("Greeting", "(re)composing")
    val usename : MutableState<String> = remember {mutableStateOf(usename)}
    Text(
        text = "Hello ${usename.value}!",
        fontSize = 25.sp,
        modifier = modifier.clickable  {
            Log.i("Greeting", "clicked")
            usename.value += " MAD"
            Log.i( "Greeting", "modified username: ${usename.value} ")
        }
    )
}
var itemCount = 0
class MediaItem (var title: String="direm", var src:String = "https://picsum.photos/75/75") {
    var createdOrModified = System.currentTimeMillis()
    var myCount = itemCount++
}

@Composable
fun ListView(mediaItems: List<MediaItem>, modifier: Modifier = Modifier) {
    Log.i("ListView", "re(composing)")
    LazyColumn {
        items(mediaItems) {mediaItem ->
            ListItemView(mediaItem, modifier = modifier)
        }
    }
}

@Composable
fun ListItemView(myItem: MediaItem, modifier: Modifier = Modifier) {
    Log.i("ListItemView", "re(composing) for: ${myItem.title} as pos ${myItem.myCount}")

  //  val  myItemState = mutableStateOf(myItem)
    val createdOrUpdatedState = mutableStateOf(myItem.createdOrModified)
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = myItem.src,
            contentDescription = myItem.title,
            modifier = modifier
                .padding(10.dp)
                .height(60.dp)
                .width(60.dp)
        )
        Column(modifier = modifier.weight(1f)) {
            Text(myItem.title, fontSize = 30.sp)
            Text(myItem.createdOrModified.toString(), fontSize = 15.sp)
            Text(createdOrUpdatedState.value.toString(), fontSize = 0.sp, modifier = modifier.height(0.dp))
        }

        IconButton(
            onClick = {
                Log.i("ListItemView", "onclick() on ${myItem}")
                myItem.createdOrModified = System.currentTimeMillis()
                createdOrUpdatedState.value = myItem.createdOrModified

            }
        ) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options",
                tint = Color.DarkGray,
                modifier = modifier.size(40.dp)
            )
        }
    }
}

fun createRandomMediaItem() : MediaItem {
    val imgs = listOf("https://picsum.photos/40/50", "https://picsum.photos/50/50", "https://picsum.photos/75/75", "https://picsum.photos/100/50")
    val titles = listOf("lorem", "ipsum","dolor", "sed", "direm")

    return MediaItem(titles.get(Random.nextInt(0,titles.size)), imgs.get(Random.nextInt(0, imgs.size)))
}

/* @Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MADDemoTheme {
        Greeting("MAD")
    }
} */