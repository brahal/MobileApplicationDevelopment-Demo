package org.dieschnittstelle.mobile.android.kotlin.skeleton.view

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.scale
import com.mapbox.geojson.Point
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.Marker
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import org.dieschnittstelle.mobile.android.kotlin.skeleton.R

class Location(val name: String, val lat: Double, val lng: Double)

@Composable
fun MapScreen() {
    val locations = listOf(
        Location("dorem", 52.51179432739056, 13.453141436381246),
        Location("lipsum", 52.52779511520585,13.40065562313563),
        Location("qllcr", 52.53791194648605, 13.409367961824364),
        Location("sed", 52.545175,13.351628)
    )

    val imgbitmap = BitmapFactory
        .decodeResource(LocalContext.current.resources, R.drawable.mapbox_logo)
        .scale(70, 70, false)

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(15.0)
            center(Point.fromLngLat(13.351628, 52.545175))
            pitch(0.0)
            bearing(0.0)
        }
    }

    val minLat = locations.minOf { it.lat }
    val maxLat = locations.maxOf { it.lat }
    val minLng = locations.minOf { it.lng }
    val maxLng = locations.maxOf { it.lng }

    val coordinateBounds = CoordinateBounds(
        Point.fromLngLat(minLng, minLat),
                Point.fromLngLat(maxLng, maxLat)
    )

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState
    ) {
        // Die spezifische Location der Iteration
        // loc: der Iterator aus der Liste
        locations.forEach {loc ->
            Log.i("MapView", "adding marker for Location $loc")

            //Marker easy to use, but not interactive
//            Marker(
//                point = Point.fromLngLat(loc.lnd, loc.lat),
//                color = Color.Red,
//                text = loc.name
//            )
            PointAnnotation(point = Point.fromLngLat(loc.lng, loc.lat)) {
                iconImage = IconImage(imgbitmap)
                textField = loc.name //entscheiden ob mit text oder ohne //clickevent implementieren
                interactionsState.onClicked {
                    true
                }
            }
            MapEffect() { mapView ->
                mapViewportState.easeTo(mapView.mapboxMap.cameraForCoordinateBounds(coordinateBounds,
                    boundsPadding = EdgeInsets(100.0, 100.0, 100.0, 100.0)
                ))
            }
        }
    }
}