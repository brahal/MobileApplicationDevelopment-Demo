package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import org.dieschnittstelle.mobile.android.kotlin.skeleton.R
import org.dieschnittstelle.mobile.android.kotlin.skeleton.model.MediaItem
import org.dieschnittstelle.mobile.android.kotlin.skeleton.viewModel.MediaAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MediaAppViewModel,
    onSelect: (MediaItem, (Boolean) -> Unit) -> Unit,
    onMenuClick: () -> Unit
) {

    // Beim Öffnen der MapScreen wird selectedMapItem zurückgesetzt
    LaunchedEffect(Unit) {
        viewModel.selectedMapItem.value = null
    }

    val context = LocalContext.current

    val items = viewModel.mediaItems
    val selectedItem by viewModel.selectedMapItem

    // ---------- Marker Bitmap ----------
    val markerBitmap = remember {
        ResourcesCompat.getDrawable(context.resources, R.drawable.marker1, null)
            ?.toBitmap(70, 120)
    }


    // ---------- Bounding Box ----------
    val minLat = items.minOfOrNull { it.latitude ?: 52.52 } ?: 52.52
    val maxLat = items.maxOfOrNull { it.latitude ?: 52.52 } ?: 52.52
    val minLng = items.minOfOrNull { it.longitude ?: 13.405 } ?: 13.405
    val maxLng = items.maxOfOrNull { it.longitude ?: 13.405 } ?: 13.405

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(
                Point.fromLngLat(
                    (minLng + maxLng) / 2,
                    (minLat + maxLat) / 2
                )
            )
            zoom(12.0)
        }
    }

    val coordinateBounds = CoordinateBounds(
        Point.fromLngLat(minLng, minLat),
        Point.fromLngLat(maxLng, maxLat)
    )

    // ---------- UI ----------
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Karte") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C2C2C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        MapboxMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            mapViewportState = mapViewportState
        ) {

            // ---------- Marker (PointAnnotation) ----------
            items.forEach { item ->
                val lat = item.latitude ?: return@forEach
                val lng = item.longitude ?: return@forEach

                Log.i("MapScreen", "Marker ${item.title} @ $lat,$lng")

                markerBitmap?.let { bitmap ->
                    PointAnnotation(Point.fromLngLat(lng, lat)) {
                        iconImage = IconImage(bitmap)
                        interactionsState.onClicked {
                            viewModel.selectedMapItem.value = item
                            true
                        }
                    }
                }
            }

            //Callout (ViewAnnotation)
            selectedItem?.let { item ->
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(
                            Point.fromLngLat(
                                item.longitude!!,
                                item.latitude!!
                            )
                        )
                        annotationAnchor {
                            anchor(ViewAnnotationAnchor.BOTTOM)
                            offsetY(62.0)
                        }
                        allowOverlap(true)
                    }
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .widthIn(min = 90.dp, max = 120.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onSelect(item) { }
                                    },
                                color = Color.Black,
                                maxLines = 1,                     // 🔴 WICHTIG
                                overflow = TextOverflow.Ellipsis // 🔴 WICHTIG
                            )

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Schließen",
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(start = 8.dp)
                                    .clickable {
                                        viewModel.selectedMapItem.value = null
                                    }
                            )
                        }
                    }
                }
            }

            MapEffect(items, selectedItem) { mapView ->
                if (items.isEmpty()) return@MapEffect

                val padding = if (selectedItem != null) {
                    EdgeInsets(220.0, 80.0, 120.0, 200.0)
                } else {
                    EdgeInsets(200.0, 120.0, 200.0, 120.0)
                }

                // ✅ NUR EINMAL – KEINE Animation
                if (!viewModel.mapCameraInitialized.value) {
                    mapView.mapboxMap.setCamera(
                        mapView.mapboxMap.cameraForCoordinateBounds(
                            coordinateBounds,
                            padding
                        )
                    )
                    viewModel.mapCameraInitialized.value = true
                }
            }
        }
    }
}
