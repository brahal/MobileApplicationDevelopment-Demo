package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import android.R
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.exifinterface.media.ExifInterface
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.*

enum class DialogMode { CREATE, EDIT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditMediaItemDialog(
    dialogShown: MutableState<Boolean>,
    mode: DialogMode,
    itemToEdit: MediaItem?,
    crudOperations: IMediaItemCRUDOperations,
    onCreated: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (!dialogShown.value) return

    Log.i("CreateEditDialog", "(re)compose mode=$mode item=${itemToEdit?.id}")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Für Upload (nur wenn REMOTE)
    val uploader = remember { RemoteMediaUploader("http://10.0.2.2:7077") }

    // ---- Initialwerte
    val initialTitle = remember(mode, itemToEdit?.id) { itemToEdit?.title.orEmpty() }
    val initialSrc = remember(mode, itemToEdit?.id) { itemToEdit?.src.orEmpty() }
    val initialStorage =
        remember(mode, itemToEdit?.id) { itemToEdit?.imageStorage ?: ImageStorage.LOCAL }

    var title by remember(mode, itemToEdit?.id) { mutableStateOf(initialTitle) }
    var src by remember(mode, itemToEdit?.id) { mutableStateOf(initialSrc) }
    var storage by remember(mode, itemToEdit?.id) { mutableStateOf(initialStorage) }

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    // Wenn REMOTE gewählt wird, brauchen wir für Upload eine lokale URI als Quelle
    var pickedLocalUri by remember(mode, itemToEdit?.id) { mutableStateOf<Uri?>(null) }

    var titleError by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }



    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        // Persistente Berechtigung
        try {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
        }

        pickedLocalUri = uri
        imageError = false

        // Vorschau:
        // - LOCAL: direkt content:// anzeigen
        // - REMOTE: Vorschau auch lokal anzeigen (content://), Upload passiert erst beim Speichern
        src = uri.toString()

        // Standortdaten MAP2

        val inputStreamExif = context.contentResolver.openInputStream(uri)
        val exif = inputStreamExif?.let { ExifInterface(it) }
        val latLong = exif?.latLong
        if (latLong != null) {
            latitude = latLong[0]
            longitude = latLong[1]
            Log.i("CreateEditDialog", "EXIF GPS: $latitude / $longitude")
        } else {
            val (randLat, randLng) = randomDefaultLocation()
            latitude = randLat
            longitude = randLng
            Log.i("CreateEditDialog", "No EXIF GPS found")
        }

        inputStreamExif?.close()

        // Dateiname auslesen
        if (title.isBlank()) {
            val cursor = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val origfilename = it.getString(
                        it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    )
                    val autoTitle = origfilename.substringBeforeLast(".")
                    title = autoTitle

                    Log.i("CreateEditDialog", "Auto-Titel gesetzt: $autoTitle")
                }
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = {
            // Änderungen verwerfen
            title = initialTitle
            src = initialSrc
            storage = initialStorage
            pickedLocalUri = null
            uploadError = null
            dialogShown.value = false
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
        ) {
            Text(
                text = if (mode == DialogMode.CREATE) "NEUES MEDIUM" else "MEDIUM EDITIEREN",
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE6E0F0))
                    .padding(12.dp),
                textAlign = TextAlign.Start
            )

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFBDB6C9))

            Column(Modifier.padding(12.dp)) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text("Titel") },
                    isError = titleError,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                pickImage.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        ) {
                            Icon(Icons.Outlined.Image, contentDescription = "Bild auswählen")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (titleError) {
                    Spacer(Modifier.height(6.dp))
                    Text("Titel Eingabe erforderlich", color = Color(0xFFB00020), fontSize = 12.sp)
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Speicherung:",
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color.Black,
                    )
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = storage == ImageStorage.LOCAL,
                            onClick = { storage = ImageStorage.LOCAL },
                            enabled = (mode == DialogMode.CREATE),
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) { Text("Lokal") }

                        SegmentedButton(
                            selected = storage == ImageStorage.REMOTE,
                            onClick = { storage = ImageStorage.REMOTE },
                            enabled = (mode == DialogMode.CREATE),
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) { Text("Remote") }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(Color.White.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = src,
                        contentDescription = "Vorschau",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (src.isBlank()) {
                        Icon(
                            Icons.Outlined.Image,
                            contentDescription = null,
                            tint = Color(0xFF6A6A6A),
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                if (imageError) {
                    Spacer(Modifier.height(6.dp))
                    Text("Bild auswählen erforderlich", color = Color(0xFFB00020), fontSize = 12.sp)
                }

                uploadError?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(it, color = Color(0xFFB00020), fontSize = 12.sp)
                }

                if (busy) {
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        enabled = (mode == DialogMode.EDIT),
                        onClick = {
                            val current = itemToEdit ?: return@TextButton
                            scope.launch(Dispatchers.IO) {
                                crudOperations.deleteItem(current.id)
                                dialogShown.value = false
                            }
                        }
                    ) { Text("LÖSCHEN") }

                    Button(
                        enabled = !busy,
                        onClick = {
                            titleError = title.isBlank()
                            imageError = src.isBlank()
                            uploadError = null
                            if (titleError || imageError) return@Button

                            scope.launch(Dispatchers.IO) {
                                try {
                                    busy = true

                                    if (mode == DialogMode.CREATE) {
                                        val finalSrc = if (storage == ImageStorage.REMOTE) {
                                            val localUri = pickedLocalUri
                                                ?: throw IllegalStateException("No pickedLocalUri for remote upload")
                                            uploader.uploadImage(context.contentResolver, localUri)
                                        } else {
                                            src // content://...
                                        }

                                        val newItem = MediaItem(
                                            title = title.trim(),
                                            src = finalSrc,
                                            imageStorage = storage,
                                            latitude = latitude,
                                            longitude = longitude
                                        ).apply {
                                            imageStorage = storage
                                        }

                                        crudOperations.createItem(newItem)
                                        onCreated?.invoke()
                                    } else {
                                        val current = itemToEdit ?: return@launch

                                        val finalSrc =
                                            if (current.imageStorage == ImageStorage.REMOTE) {
                                                // Anforderung 3: bei existierendem Item bleibt die Speicherart wie bei Erstellung
                                                val localUri = pickedLocalUri
                                                if (localUri != null) uploader.uploadImage(
                                                    context.contentResolver,
                                                    localUri
                                                ) else current.src
                                            } else {
                                                // LOCAL: wenn neues Bild gewählt, ist src schon content://..., sonst bleibt es
                                                src
                                            }

                                        current.title = title.trim()
                                        current.src = finalSrc
                                        current.createdOrModified = System.currentTimeMillis()
                                        crudOperations.updateItem(current.id, current)
                                    }

                                    dialogShown.value = false
                                } catch (e: Exception) {
                                    uploadError = "Speichern/Upload fehlgeschlagen: ${e.message}"
                                } finally {
                                    busy = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1DB954),
                            contentColor = Color.White
                        )
                    ) {
                        Text(if (mode == DialogMode.CREATE) "HINZUFÜGEN" else "SPEICHERN")
                    }
                }
            }
        }
    }
}