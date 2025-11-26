package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import ListView
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.dieschnittstelle.mobile.android.kotlin.skeleton.MediaAppScreens
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.createRandomMediaItem

@Composable
fun OverviewScreen(
    navController: NavHostController,
    mediaItems: MutableList<MediaItem>,
    crudOperations: IMediaItemCRUDOperations,
    modifier: Modifier = Modifier
) {
    Log.i("OverviewScreen", "(re)composing")

    val progressDialogShown = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scrollToEnd = remember { mutableStateOf(false) }

    LaunchedEffect(mediaItems.isEmpty()) {
        if (mediaItems.isEmpty()) {
            coroutineScope.launch(Dispatchers.IO) {
                progressDialogShown.value = true
                crudOperations.readAllItems()
                progressDialogShown.value = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton({
                val newItem = createRandomMediaItem()
                Log.i("ListView (FAB)", "onclick()")
                Log.i("Add new Item", "onclick()")

                coroutineScope.launch(Dispatchers.IO) {
                    scrollToEnd.value = true
                    crudOperations.createItem(newItem)

                }
                Log.i("Add new Item", "added new Item to List")
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    ) { innerPadding ->
        if (progressDialogShown.value) {
            ProgressDialog(progressDialogShown)
        }

        ListView(
            mediaItems,
            scrollToEnd,
            onSelect = { item, ondoneCallback ->
//                item.title += (" " + item.title)
//                item.createdOrModified = System.currentTimeMillis()
//
//                Thread {
//                    crudOperations.updateItem(item.id, item)
//                    ondoneCallback.invoke(true)
//                }.start()
                navController.navigate(item)

            },
            onOptions = { item ->
                Log.i("ListView", "removing: ${item.title}")

                coroutineScope.launch(Dispatchers.IO) {
                    progressDialogShown.value = true
                    crudOperations.deleteItem(item.id)
                    progressDialogShown.value = false
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}