package org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.IMediaItemCRUDOperations
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.RoomLocalMediaItemCRUDOperationsImpl
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.SimpleMediaItemCRUDOperationsImpl
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.createRandomMediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.DialogMode
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.FilterMode


enum class MainViewMode {
    MEDIA,   // Listenansicht
    MAP      // Kartenansicht
}

class MediaAppViewModel: ViewModel() {

    // aktueller Hauptmodus
    val mainViewMode = mutableStateOf(MainViewMode.MEDIA)

    // Drawer offen / zu
    val drawerOpen = mutableStateOf(false)


    // Daten
    val mediaItems = mutableStateListOf<MediaItem>()

    // CRUD
    var crudOperations: IMediaItemCRUDOperations = SimpleMediaItemCRUDOperationsImpl( mediaItems)

    // Dialog Zustand
    val createEditDialogShown = mutableStateOf(false)
   val dialogMode = mutableStateOf(DialogMode.CREATE)
   val itemToBeEdited = mutableStateOf<MediaItem?>(null)
   val progressDialogShown = mutableStateOf(false)
    // Filter
    val filterMode = mutableStateOf(FilterMode.ALL)

    fun loadData() {
        progressDialogShown.value = true
        viewModelScope.launch(Dispatchers.IO) {
            crudOperations.readAllItems()
            progressDialogShown.value = false
        }
    }
}