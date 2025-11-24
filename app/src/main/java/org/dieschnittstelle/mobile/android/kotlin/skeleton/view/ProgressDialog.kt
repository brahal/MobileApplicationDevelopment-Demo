package org.dieschnittstelle.mobile.android.kotlin.skeleton.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ProgressDialog(dialogShown: MutableState<Boolean>) {
    Dialog(
        onDismissRequest = {
            dialogShown.value = false
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            CircularProgressIndicator()
        }
    }
}