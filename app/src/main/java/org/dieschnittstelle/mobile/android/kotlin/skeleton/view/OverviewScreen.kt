package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import ListView
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.dieschnittstelle.mobile.android.kotlin.skeleton.MediaAppScreens
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.createRandomMediaItem

@Composable
fun OverviewScreen(navController: NavHostController , mediaItems: MutableList<MediaItem>, crudOperations: IMediaItemCRUDOperations, modifier: Modifier= Modifier) {
    Log.i("OverviewScreen", "(re)composing" )

    val progressDialogShown = remember {mutableStateOf(false)}
    Thread {
        progressDialogShown.value = true
        crudOperations.readAllItems()
        progressDialogShown.value = false
    }.start()

    Scaffold(modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton({
                val newItem = createRandomMediaItem()
                Log.i("ListView (FAB)", "onclick()" )
                Log.i("Add new Item", "onclick()" )

                Thread {
                    crudOperations.createItem(newItem)
                }.start()
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
        if (progressDialogShown.value) {
            ProgressDialog(progressDialogShown)
        }

        ListView(
            mediaItems,
            onSelect= {
                item, ondoneCallback ->
//                item.title += (" " + item.title)
//                item.createdOrModified = System.currentTimeMillis()
//
//                Thread {
//                    crudOperations.updateItem(item.id, item)
//                    ondoneCallback.invoke(true)
//                }.start()
                navController.navigate(item)

            },
            onOptions = {item->
                Log.i("ListView", "removing: ${item.title}")

                Thread {
                    progressDialogShown.value = true
                    crudOperations.deleteItem(item.id)
                    progressDialogShown.value = false
                }.start()
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}