package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import ListView
import android.R.attr.label
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.dieschnittstelle.mobile.android.kotlin.skeleton.MediaAppScreens
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.ImageStorage
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.createRandomMediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel.MediaAppViewModel

enum class FilterMode { ALL, LOCAL_ONLY, REMOTE_ONLY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    navController: NavHostController,
    viewModel: MediaAppViewModel,
    modifier: Modifier = Modifier
) {
    Log.i("OverviewScreen", "(re)composing")

    val progressDialogShown = viewModel.progressDialogShown
    val actionDialogShown = remember { mutableStateOf(false) }


    // EIN Dialog für Create + Edit
    val createEditDialogShown = viewModel.createEditDialogShown
    val dialogMode = viewModel.dialogMode

    val filterMode = viewModel.filterMode
    val coroutineScope = rememberCoroutineScope()

    // Das Item, das editiert/gelöscht werden soll
    val itemToBeEdited = viewModel.itemToBeEdited

    val scrollToEnd = remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.mediaItems.isEmpty()) {
        if (viewModel.mediaItems.isEmpty()) {
            viewModel.loadData()
        }
    }

    val filteredItems: MutableList<MediaItem> = remember(viewModel.mediaItems, filterMode.value) {
        when (filterMode.value) {
            FilterMode.ALL -> viewModel.mediaItems
            FilterMode.LOCAL_ONLY -> viewModel.mediaItems.filter { it.imageStorage == ImageStorage.LOCAL }.toMutableList()
            FilterMode.REMOTE_ONLY -> viewModel.mediaItems.filter { it.imageStorage == ImageStorage.REMOTE }.toMutableList()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        contentColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Medien") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu,
                            contentDescription = "Menü",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                actions = {
                    IconButton({
//                        dialogMode.value = DialogMode.CREATE
//                        itemToBeEdited.value = null
//                        createEditDialogShown.value = true
                        viewModel.dialogMode.value = DialogMode.CREATE
                        viewModel.itemToBeEdited.value = null
                        viewModel.createEditDialogShown.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =  Color(0xFF181818),
                   // containerColor = Color.DarkGray,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
                tonalElevation = 0.dp
            ) {
                TextButton(
                    onClick = {
                        filterMode.value = when (filterMode.value) {
                            FilterMode.ALL -> FilterMode.LOCAL_ONLY
                            FilterMode.LOCAL_ONLY -> FilterMode.REMOTE_ONLY
                            FilterMode.REMOTE_ONLY -> FilterMode.ALL
                        }
                    }
                ) {
                    Text("FILTER: $label", color = Color.White)
//                    Später durch das ersetzen
//                    Text(
//                        text = when (filterMode.value) {
//                            FilterMode.ALL -> "FILTER: Alle"
//                            FilterMode.LOCAL_ONLY -> "FILTER: Lokal"
//                            FilterMode.REMOTE_ONLY -> "FILTER: Remote"
//                        },
//                        color = Color.White
//                    )
                }
            }
        },
    ) { innerPadding ->
        if (progressDialogShown.value) {
            ProgressDialog(progressDialogShown)
        }

        // 3-Punkte Menü (ActionDialog)
        if (actionDialogShown.value && itemToBeEdited.value != null) {
            ActionDialog(
                actionDialogShown = actionDialogShown,
                // NEU: wir öffnen den Editor über createDialogShown + dialogMode
                createDialogShown = createEditDialogShown,
                dialogMode = dialogMode,
                itemToBeEdited = itemToBeEdited.value!!,   // ✅ HIER
                crudOperations = viewModel.crudOperations
            )
        }

        // Create/Edit Dialog (ein Dialog für beides)
        if (createEditDialogShown.value) {
            CreateEditMediaItemDialog(
                dialogShown = createEditDialogShown,
                mode = dialogMode.value,
                itemToEdit = if (dialogMode.value == DialogMode.EDIT) itemToBeEdited.value else null,
                crudOperations = viewModel.crudOperations,
                onCreated = { scrollToEnd.value = true }
            )
        }

        ListView(
            viewModel.mediaItems, //Später durch filteredItems ersetzen
            scrollToEnd,
            onSelect = { item, ondoneCallback ->
                navController.navigate(item)

            },
            onOptions = { item ->
                // 3 Punkte => ActionDialog
                itemToBeEdited.value = item      // ✅ wichtig: echtes Item merken
                actionDialogShown.value = true
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}