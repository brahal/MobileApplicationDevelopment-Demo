package org.dieschnittstelle.mobile.android.kotlin.skeleton

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.OverviewScreen
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.RoomLocalMediaItemCRUDOperationsImpl
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.createRandomMediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.ui.theme.MADDemoTheme
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.MapScreen
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.ReadviewScreen
import org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel.MediaAppViewModel

enum class MediaAppScreens {OVERVIEW, READVIEW, MapView}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()

        val viewModel: MediaAppViewModel by viewModels()
        viewModel.crudOperations = RoomLocalMediaItemCRUDOperationsImpl(this,viewModel.mediaItems) //SimpleMediaItemCRUDOperationsImpl(mediaItems)

        setContent {
            val navController = rememberNavController()

            MADDemoTheme() {
                NavHost(
                    navController = navController,
                    startDestination = MediaAppScreens.OVERVIEW.name,
                ) {
                    composable(MediaAppScreens.OVERVIEW.name) {
                        OverviewScreen(navController,viewModel)
                    }
                    composable<MediaItem>() {backstackEntry ->
                        val selectedItem = backstackEntry.toRoute<MediaItem>()
                        ReadviewScreen(navController, selectedItem, viewModel.crudOperations)
                    }
                    composable(MediaAppScreens.MapView.name) {
                        MapScreen()
                    }
                }
            }
        }
    }
}
