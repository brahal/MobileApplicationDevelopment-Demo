//package org.dieschnittstelle.mobile.android.kotlin.skeleton.view
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
//import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
//
//enum class EditorMode { CREATE, EDIT }
//
//private const val DEFAULT_IMAGE_URL = "https://picsum.photos/75/75"
//
//@Composable
//fun MediaItemEditorDialog(
//    isOpen: Boolean,
//    mode: EditorMode,
//    item: MediaItem?,
//    onDismiss: () -> Unit,
//    crud: IMediaItemCRUDOperations,
//    onDone: (Boolean) -> Unit
//) {
//    if (!isOpen) return
//
//    val scope = rememberCoroutineScope()
//
//    val titleState = remember(mode, item?.id) { mutableStateOf(item?.title ?: "") }
//
//
//
//    val focusRequester = remember { FocusRequester() }
//    LaunchedEffect(isOpen) { focusRequester.requestFocus() }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//
//        // ✅ Titel wie im Screenshot
//        title = {
//            Text(
//                text = if (mode == EditorMode.CREATE) "NEUES MEDIUM" else "MEDIUM EDITIEREN"
//            )
//        },
//
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = titleState.value,
//                    onValueChange = { titleState.value = it },
//                    label = { Text("Name") },
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .focusRequester(focusRequester)
//                )
//            }
//        },
//
//        // ✅ nur diese Buttons unten (kein Abbrechen)
//        confirmButton = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // LÖSCHEN (nur aktiv im EDIT-Fall)
//                TextButton(
//                    enabled = (mode == EditorMode.EDIT),
//                    onClick = {
//                        val current = item ?: return@TextButton
//                        scope.launch(Dispatchers.IO) {
//                            crud.deleteItem(current.id)
//                            launch(Dispatchers.Main) {
//                                onDone(false)
//                                onDismiss()
//                            }
//                        }
//                    }
//                ) {
//                    Text("LÖSCHEN")
//                }
//
//                // HINZUFÜGEN / SPEICHERN (grün wie im Bild)
//                Button(
//                    onClick = {
//                        val title = titleState.value.trim()
//                        if (title.isEmpty()) return@Button
//
//                        scope.launch(Dispatchers.IO) {
//                            if (mode == EditorMode.CREATE) {
//                                crud.createItem(MediaItem(title = title))
//                            } else {
//                                val current = item ?: return@launch
//                                current.title = title
//                                current.createdOrModified = System.currentTimeMillis()
//                                crud.updateItem(current.id, current)
//                            }
//
//                            launch(Dispatchers.Main) {
//                                onDone(mode == EditorMode.CREATE)
//                                onDismiss()
//                            }
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF1DB954), // ✅ Spotify-Grün
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text(if (mode == EditorMode.CREATE) "HINZUFÜGEN" else "SPEICHERN")
//                }
//            }
//        }
//
//        // ✅ kein dismissButton -> kein Abbrechen Button
//    )
//}