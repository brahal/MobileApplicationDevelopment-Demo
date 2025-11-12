package org.dieschnittstelle.mobile.android.kotlin.skeleton

import ListView
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.SimpleMediaItemCRUDOperationsImpl
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.createRandomMediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.ui.theme.MADDemoTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()

        val usename = mutableStateOf("MAD")

        val mediaItems = mutableStateListOf<MediaItem>()
        val crudOperations = SimpleMediaItemCRUDOperationsImpl(mediaItems)
        crudOperations.readAllItems()

        setContent {
            MADDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton({
                            val newItem = createRandomMediaItem()
                            Log.i("ListView (FAB)", "onclick()" )
                            Log.i("Add new Item", "onclick()" )
                            crudOperations.createItem(newItem)
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



/* @Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MADDemoTheme {
        Greeting("MAD")
    }
} */