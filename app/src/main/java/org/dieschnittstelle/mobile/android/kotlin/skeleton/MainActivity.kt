package org.dieschnittstelle.mobile.android.kotlin.skeleton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.OverviewScreen
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.RoomLocalMediaItemCRUDOperationsImpl
import org.dieschnittstelle.mobile.android.kotlin.skeleton.ui.theme.MADDemoTheme
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.MapScreen
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.ReadviewScreen
import org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel.MainViewMode
import org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel.MediaAppViewModel

enum class MediaAppScreens { OVERVIEW, READVIEW, MapView }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MediaAppViewModel by viewModels()

        viewModel.crudOperations = RoomLocalMediaItemCRUDOperationsImpl(
            this,
            viewModel.mediaItems
        ) //SimpleMediaItemCRUDOperationsImpl(mediaItems)

        // Einstiegspunkt für Jetpack Compose, ab hier wird die UI deklarativ aufgebaut
        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            MADDemoTheme() {
                // Navigation Drawer als Seitenmenü
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(
                                "Menü",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                            NavigationDrawerItem(
                                label = { Text("Medien") },
                                selected = viewModel.mainViewMode.value == MainViewMode.MEDIA,
                                onClick = {
                                    viewModel.mainViewMode.value = MainViewMode.MEDIA
                                    navController.navigate(MediaAppScreens.OVERVIEW.name) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    // Drawer schließen erfolgt in einer Coroutine, da close() eine suspend-Funktion ist
                                    scope.launch { drawerState.close() }
                                },
                                icon = { Icon(Icons.Filled.List, contentDescription = "Medien") }
                            )

                            NavigationDrawerItem(
                                label = { Text("Karte") },
                                selected = viewModel.mainViewMode.value == MainViewMode.MAP,
                                onClick = {
                                    viewModel.mainViewMode.value = MainViewMode.MAP
                                    navController.navigate(MediaAppScreens.MapView.name) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }

                                    scope.launch { drawerState.close() }
                                },
                                icon = { Icon(Icons.Filled.Map, contentDescription = "Karte") }
                            )
                        }
                    }
                )
                {
                    // NavHost definiert den Startpukt
                    NavHost(
                        navController = navController,
                        startDestination = MediaAppScreens.OVERVIEW.name,
                    ) {
                        composable(MediaAppScreens.OVERVIEW.name) {
                            OverviewScreen(
                                navController,
                                viewModel,
                                onMenuClick = { scope.launch { drawerState.open() } })
                        }
                        composable<MediaItem>() { backstackEntry ->
                            val selectedItem = backstackEntry.toRoute<MediaItem>()
                            ReadviewScreen(
                                navController, selectedItem, viewModel.crudOperations, viewModel,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }
                        composable(MediaAppScreens.MapView.name) {
                            MapScreen(
                                viewModel,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onSelect = { item, callback ->
                                    navController.navigate(item)
                                    callback(true)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
