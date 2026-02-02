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
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel.MediaAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionDialog(
    actionDialogShown: MutableState<Boolean>,
    createDialogShown: MutableState<Boolean>,
    dialogMode: MutableState<DialogMode>,
    itemToBeEdited: MediaItem,
    viewModel: MediaAppViewModel
) {

    // Wenn das Dialog-Flag false ist, wird gar nichts gerendert
    if (!actionDialogShown.value) return

    Dialog(
        onDismissRequest = { actionDialogShown.value = false }, // Schließen bei Zurück oder Außenklick
        properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)
    ) {
        // Box dient als halbtransparenter Hintergrund
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f)) // Abduklung der Liste
                .clickable { actionDialogShown.value = false }, // Klick außerhalb schlißt Dialog
            contentAlignment = Alignment.Center
        ) {
            // Menü-Panel selbst
            Surface(
                modifier = Modifier
                    .padding(start = 12.dp, top = 46.dp)
                    .width(240.dp)
                    .clickable(enabled = false) {}, // Klicks im Menü sollen NICHT den Dialog schließen
                color = Color(0xFF333333)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2A2A2A))
                    ) {
                        Text(
                            text = itemToBeEdited.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFF3A3A3A))

                    MenuRow("Löschen") {
                        actionDialogShown.value = false
                        viewModel.deleteConfirmDialogShown.value = true

                    }

                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    MenuRow("Editieren") {
                        actionDialogShown.value = false  // Aktionsmenü schließen
                        dialogMode.value = DialogMode.EDIT  // Dialogmodus auf EDIT setzen
                        createDialogShown.value = true  // Create/Edit-Dialog öffnen
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
