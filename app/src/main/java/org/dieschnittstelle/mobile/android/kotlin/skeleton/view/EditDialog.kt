//package org.dieschnittstelle.mobile.android.kotlin.skeleton.view
//import android.util.Log
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.BasicAlertDialog
//import androidx.compose.material3.Card
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.DialogProperties
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
//import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EditDialog(editDialogShown: MutableState<Boolean>, itemToBeEdited: MediaItem, crudOperations: IMediaItemCRUDOperations, modifier: Modifier = Modifier) {
//    Log.i("EditDialog", "(re)composing")
//    val titleFieldInput = remember { mutableStateOf(itemToBeEdited.title) }
//
//    val coroutineScope = rememberCoroutineScope()
//
//    BasicAlertDialog(
//        onDismissRequest = {
//         //   editDialogShown.value = true
//        },
//        properties = DialogProperties(),
//    ) {
//        Card(
//            modifier = modifier.wrapContentWidth()
//                .wrapContentHeight(),
//            shape = RoundedCornerShape(16.dp)
//        )   {
//            Log.i("EditDialog", "(re)composing Card")
//            Text(text = "Editieren",
//                fontSize = 20.sp,
//                modifier = modifier.padding(12.dp).wrapContentSize(Alignment.Center),
//                textAlign = TextAlign.Center)
//            HorizontalDivider(modifier = modifier, thickness = 3.dp)
//            TextField(value = titleFieldInput.value, onValueChange = {
//                newText ->
//                Log.i("EditDialog", "onValueChange $newText")
//                titleFieldInput.value= newText
//                itemToBeEdited.title = newText
//            })
//            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//                TextButton({
//                    editDialogShown.value = false
//                }, modifier = modifier.padding(12.dp)) {
//                    Text(text ="Cancel", fontSize = 20.sp, modifier = modifier)
//                }
//                TextButton({
//                    coroutineScope.launch(Dispatchers.IO) {
//                        crudOperations.updateItem(itemToBeEdited.id, itemToBeEdited)
//                        editDialogShown.value = false
//                    }
//                }, modifier = modifier.padding(12.dp)) {
//                    Text(text ="Save", fontSize = 20.sp)
//
//                }
//            }
//        }
//    }
//}