package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.dieschnittstelle.mobile.android.kotlin.skeleton.MediaAppScreens
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadviewScreen(navController: NavHostController , item: MediaItem, modifier: Modifier = Modifier) {
    Log.i("ReadviewScreen", "(re)composing for ${item.title}" )
  //  Text(item.title, modifier)
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        modifier = modifier.size(30.dp))
                },
                title={Text(item.title)},
                actions = {
                    IconButton({
                        Log.i("ReadviewScreen Basma", "deleting ${item.title}")
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = modifier.size(35.dp))
                    }
                },
                modifier=modifier
            )
        }
    ) { innerPadding ->

        AsyncImage(model=item.src,
            contentDescription = item.title,
            modifier.padding(innerPadding).fillMaxWidth())

    }
}