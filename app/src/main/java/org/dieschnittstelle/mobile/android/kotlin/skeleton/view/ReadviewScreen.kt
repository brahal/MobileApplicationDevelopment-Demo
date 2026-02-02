package org.dieschnittstelle.mobile.android.kotlin.skeleton.view


import LocationMap
import androidx.exifinterface.media.ExifInterface
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel.MediaAppViewModel
import java.io.File
import java.io.FileOutputStream

// Detail-/Leseansicht (BAS4): zeigt ein MediaItem, erlaubt Bildauswahl (FRM1) und CRUD-Aktionen.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadviewScreen(
    navController: NavHostController,
    item: MediaItem,
    crudOperations: IMediaItemCRUDOperations,
    viewModel: MediaAppViewModel,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Zugriff auf Android Context innerhalb von Compose, z.B. für ContentResolver und Dateisystem.
    val context = LocalContext.current
    // UI-State
    val imgScrState = remember { mutableStateOf(item.src) }
    val isDirtyState = remember { mutableStateOf(false) }
    val isEditing = remember { mutableStateOf(false) }

    // temporäre Koordinaten (nur im Edit-Modus)
    val tempLatitude = remember { mutableStateOf(item.latitude) }
    val tempLongitude = remember { mutableStateOf(item.longitude) }

    val showMap = remember {mutableStateOf(true)}

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->

        val cursor = context.contentResolver.query(uri!!, null, null, null, null)
        var filename = "${System.currentTimeMillis()}.jpg"
        cursor?.use {
            if (it.moveToFirst()) {
                val origfilename =
                    it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                val size = it.getLong(it.getColumnIndexOrThrow(OpenableColumns.SIZE))
                Log.i("ReadviewScreen", "file has name got $origfilename and size $size")
                filename = origfilename
            }
        }

        isEditing.value = true
        isDirtyState.value = true

        // copy the file to the local files directory
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, filename)
        val outputStream = FileOutputStream(file)

        // copy to the local stream
        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        // context.contentResolver.takePersistableUriPermission(uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val localFile = "file://$file"
        imgScrState.value = localFile

        // EXIF-Metadaten: Auslesen von GPS (lat/long), um Standortdaten für Kartenfunktionalität zu nutzen.
        val inputStreamExif = context.contentResolver.openInputStream(uri)
        val exif = inputStreamExif?.let { ExifInterface(it) }

        val latLong = exif?.latLong

        val baseLat = 52.5200
        val baseLng = 13.4050

        val offset = (item.id % 10) * 0.001
        if (latLong != null) {

            tempLatitude.value = latLong[0]
            tempLongitude.value = latLong[1]
        } else {
            // Default-Standort
            val baseLat = 52.5200
            val baseLng = 13.4050
            val offset = (item.id % 10) * 0.001

            tempLatitude.value = baseLat + offset
            tempLongitude.value = baseLng + offset
        }
        inputStreamExif?.close()

    }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            modifier = modifier.size(30.dp)
                        )
                    }
                },
                title = { Text(item.title) },
                actions = {

                    if (isDirtyState.value) {
                        IconButton({
                            coroutineScope.launch(Dispatchers.IO) {
                                //beim Speichern übernehmen!
                                item.src = imgScrState.value
                                item.latitude = tempLatitude.value
                                item.longitude = tempLongitude.value
                                item.createdOrModified = System.currentTimeMillis()

                                crudOperations.updateItem(item.id, item)
                                coroutineScope.launch(Dispatchers.Main) {
                                    isEditing.value = false
                                    navController.popBackStack()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Save Media Item",
                                modifier = modifier.size(35.dp)
                            )
                        }
                    }

                    IconButton({
                        pickMedia.launch("image/*")
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Select Image",
                            modifier = modifier.size(35.dp)
                        )
                    }

                    IconButton({
                        viewModel.itemToBeEdited.value = item
                        viewModel.deleteConfirmDialogShown.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = modifier.size(35.dp)
                        )
                    }
                },
                modifier = modifier
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
                tonalElevation = 0.dp
            ) {
                //Um Lifecycle-Problemen zu verhindern
                IconButton(onClick = {
                    showMap.value = false
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Zurück",
                        tint = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        if (viewModel.deleteConfirmDialogShown.value &&
            viewModel.itemToBeEdited.value != null
        ) {
            DeleteConfirmDialog(
                dialogShown = viewModel.deleteConfirmDialogShown,
                item = viewModel.itemToBeEdited.value!!,
                onConfirmDelete = {
                    coroutineScope.launch(Dispatchers.IO) {
                        crudOperations.deleteItem(item.id)

                        launch(Dispatchers.Main) {
                            viewModel.deleteConfirmDialogShown.value = false
                            navController.popBackStack()
                        }
                    }
                },
                onDismiss = {
                    viewModel.deleteConfirmDialogShown.value = false
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
                .verticalScroll(scrollState),
        ) {
            AsyncImage(
                model = imgScrState.value,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            // Abstand
            Spacer(modifier = Modifier.height(12.dp))

            val currentLat = if (isEditing.value) tempLatitude.value else item.latitude
            val currentLng = if (isEditing.value) tempLongitude.value else item.longitude

            if (
                showMap.value &&
                currentLat != null &&
                currentLng != null
            ) {
                Spacer(modifier = Modifier.padding(8.dp))
                key(currentLat, currentLng) {
                    LocationMap(
                        latitude = currentLat,
                        longitude = currentLng,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
