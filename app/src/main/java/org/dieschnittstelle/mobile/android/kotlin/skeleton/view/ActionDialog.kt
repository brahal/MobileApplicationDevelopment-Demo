package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionDialog(
    actionDialogShown: MutableState<Boolean>,
    createDialogShown: MutableState<Boolean>,
    dialogMode: MutableState<DialogMode>,
    itemToBeEdited: MediaItem,
    crudOperations: IMediaItemCRUDOperations,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()

    if (!actionDialogShown.value) return

    Dialog(
        onDismissRequest = { actionDialogShown.value = false },
        properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)
    ) {
        // Hintergrund (Scrim) + Klick außerhalb schließt
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f))
                .clickable { actionDialogShown.value = false },
            contentAlignment = Alignment.Center
        ) {
            // Menü-Panel oben links
            Surface(
                modifier = Modifier
                    .padding(start = 12.dp, top = 46.dp) // 👈 Position wie Screenshot
                    .width(240.dp)
                    .clickable(enabled = false) {},      // Klick im Panel schließt nicht
                color = Color(0xFF333333),
                tonalElevation = 0.dp
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2A2A2A))
                    ) {
                        Text(
                            text = itemToBeEdited.title,
                            color = Color(0xFFB0B0B0),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFF3A3A3A))

                    MenuRow("Löschen") {
                        actionDialogShown.value = false
                        scope.launch(Dispatchers.IO) {
                            crudOperations.deleteItem(itemToBeEdited.id)
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFF404040))

                    MenuRow("Editieren") {
                        actionDialogShown.value = false
                        dialogMode.value = DialogMode.EDIT
                        createDialogShown.value = true
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Text(text = label, color = Color.White, fontSize = 16.sp)
    }
}

//    ModalBottomSheet(
//        onDismissRequest = {
//            actionDialogShown.value = false
//        },
//        sheetState = sheetState
//    ) {
//        Column {
//            Text(itemToBeEdited.title, fontSize = 30.sp, modifier = modifier.padding(12.dp))
//            HorizontalDivider(modifier = modifier, thickness = 3.dp)
//            Row(modifier = modifier
//                .fillMaxWidth()
//                .clickable {
//                    sheetCoroutineScope.launch() {
//                        sheetState.hide()
//                    }.invokeOnCompletion {
//                        actionDialogShown.value = false
//                        editDialogShown.value = true
//                    }
//
//                }) {
//                Text("Edit", fontSize = 30.sp, modifier = modifier.padding(12.dp))
//            }
//            Row(modifier = modifier
//                .fillMaxWidth()
//                .clickable {
//                    sheetCoroutineScope.launch(Dispatchers.IO) {
//                        crudOperations.deleteItem(itemToBeEdited.id)
//                    }.invokeOnCompletion {
//                        sheetCoroutineScope.launch() {
//                            sheetState.hide()
//                            actionDialogShown.value = false
//                        }
//                    }
//
//
//                }) {
//                Text("Delete", fontSize = 30.sp, modifier = modifier.padding(12.dp))
//            }
//        }
//    }
//}