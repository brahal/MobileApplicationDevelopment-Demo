package org.dieschnittstelle.mobile.android.kotlin.skeleton

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import org.dieschnittstelle.mobile.android.kotlin.skeleton.view.ReadviewScreen

enum class MediaAppScreens {OVERVIEW, READVIEW}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()

        val usename = mutableStateOf("MAD")

        val mediaItems = mutableStateListOf<MediaItem>()
        val crudOperations = RoomLocalMediaItemCRUDOperationsImpl(this,mediaItems) //SimpleMediaItemCRUDOperationsImpl(mediaItems)

        setContent {
            val navController = rememberNavController()

            MADDemoTheme {
                NavHost(
                    navController = navController,
                    startDestination = MediaAppScreens.OVERVIEW.name,
                ) {
                    composable(MediaAppScreens.OVERVIEW.name) {
                        OverviewScreen(navController,mediaItems, crudOperations)
                    }
                    composable<MediaItem>() {backstackEntry ->
                        val selectedItem = backstackEntry.toRoute<MediaItem>()
                        ReadviewScreen(navController, selectedItem, crudOperations)
                    }
                }
            }
        }
    }
}


/* @Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MADDemoTheme {
        Greeting("MAD")
    }
} */