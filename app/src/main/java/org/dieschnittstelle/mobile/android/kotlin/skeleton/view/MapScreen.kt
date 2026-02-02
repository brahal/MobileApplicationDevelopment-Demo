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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.EdgeInsets
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

    LaunchedEffect(Unit) {
        viewModel.selectedMapItem.value = null
    }

    val context = LocalContext.current
    val items = viewModel.mediaItems
    val selectedItem by viewModel.selectedMapItem


    val markerBitmap = remember {
        ResourcesCompat.getDrawable(context.resources, R.drawable.marker1, null)
            ?.toBitmap(70, 120)
    }

    var mapVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF181818),
        contentColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Karte") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü", modifier = Modifier.size(48.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C2C2C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        MapboxMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .alpha(if (mapVisible) 1f else 0f)
        ) {

            // MARKER
            items.forEach { item ->
                Log.d(
                    "MapCoords",
                    "ID=${item.id}, title=${item.title}, lat=${item.latitude}, lon=${item.longitude}"
                )
                PointAnnotation(
                    Point.fromLngLat(item.longitude!!, item.latitude!!)
                ) {
                    iconImage = IconImage(markerBitmap!!)
                    interactionsState.onClicked {
                        viewModel.selectedMapItem.value = item
                        true
                    }
                }
            }

            // CALLOUT
            selectedItem?.let { item ->
                key(item.id) {
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(Point.fromLngLat(item.longitude!!, item.latitude!!))
                        annotationAnchor {
                            anchor(ViewAnnotationAnchor.BOTTOM)
                            offsetY(62.0)
                        }
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
            }

            MapEffect(items) { mapView ->
                mapView.mapboxMap.subscribeMapLoaded {

                    if (items.isEmpty()) {
                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(13.4050, 52.5200)) // Berlin Fallback
                                .zoom(5.0)
                                .build()
                        )
                        mapVisible = true
                        return@subscribeMapLoaded
                    }

                    val points = items.map {
                        Point.fromLngLat(it.longitude!!, it.latitude!!)
                    }

                    if (points.size == 1) {
                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(points.first())
                                .zoom(14.0)
                                .build()
                        )
                    } else {
                        val southwest = Point.fromLngLat(
                            points.minOf { it.longitude() },
                            points.minOf { it.latitude() }
                        )
                        val northeast = Point.fromLngLat(
                            points.maxOf { it.longitude() },
                            points.maxOf { it.latitude() }
                        )

                        val camera = mapView.mapboxMap.cameraForCoordinateBounds(
                            CoordinateBounds(southwest, northeast),
                            EdgeInsets(200.0, 200.0, 200.0, 200.0)
                        )

                        mapView.mapboxMap.setCamera(camera)
                    }

                    mapVisible = true
                }
            }
        }
    }
}