import com.mapbox.geojson.Point
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import org.dieschnittstelle.mobile.android.kotlin.skeleton.R

@Composable
fun LocationMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val markerBitmap = remember {
        ResourcesCompat.getDrawable(context.resources, R.drawable.marker1, null)
            ?.toBitmap(70, 120)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(longitude, latitude))
            zoom(14.0)
        }
    }

    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState
    ) {
        markerBitmap?.let { bitmap ->
            PointAnnotation(
                point = Point.fromLngLat(longitude, latitude)
            ) {
                iconImage = IconImage(bitmap)
            }
        }
    }
}