package org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.SimpleMediaItemCRUDOperationsImpl
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.DialogMode
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.FilterMode


enum class MainViewMode {
    MEDIA,   // Listenansicht
    MAP      // Kartenansicht
}

class MediaAppViewModel : ViewModel() {

    val mainViewMode = mutableStateOf(MainViewMode.MEDIA)

    val mediaItems = mutableStateListOf<MediaItem>()

    var crudOperations: IMediaItemCRUDOperations = SimpleMediaItemCRUDOperationsImpl(mediaItems)

    val createEditDialogShown = mutableStateOf(false)
    val dialogMode = mutableStateOf(DialogMode.CREATE)
    val itemToBeEdited = mutableStateOf<MediaItem?>(null)

    val progressDialogShown = mutableStateOf(false)
    val deleteConfirmDialogShown = mutableStateOf(false)

    val filterMode = mutableStateOf(FilterMode.ALL)

    val selectedMapItem = mutableStateOf<MediaItem?>(null)

    // Daten laden
    fun loadData() {
        progressDialogShown.value = true
        viewModelScope.launch(Dispatchers.IO) {
            crudOperations.readAllItems()
            progressDialogShown.value = false
        }
    }
}