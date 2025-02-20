package com.example.stagerredphotogallery
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.xmlpull.v1.XmlPullParser
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.stagerredphotogallery.ui.theme.StagerredPhotoGalleryTheme

data class PhotoItem(val filename: String, val title: String)

fun loadPhotosFromXml(context: Context): List<PhotoItem> {
    val photos = mutableListOf<PhotoItem>()
    val parser = context.resources.getXml(R.xml.photos)

    var filename = ""
    var title = ""

    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
        when (parser.eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "filename" -> filename = parser.nextText()
                    "title" -> title = parser.nextText()
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "photo") {
                    photos.add(PhotoItem(filename, title))
                }
            }
        }
        parser.next()
    }
    return photos
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Color.White) {
                    PhotoGalleryScreen()
                }
            }
        }
    }
}

@Composable
fun PhotoGalleryScreen() {
    val context = LocalContext.current
    val photos = remember { loadPhotosFromXml(context) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp), // Staggered grid with adaptive sizing
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photo ->
            PhotoItemView(photo)
        }
    }
}

@Composable
fun PhotoItemView(photo: PhotoItem) {
    var isExpanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isExpanded) 1.5f else 1f)

    Box(modifier = Modifier
        .clickable {
            isExpanded = !isExpanded
        }
        .padding(4.dp)
        .fillMaxWidth()
    ) {
        val context = LocalContext.current
        val drawableId = context.resources.getIdentifier(photo.filename, "drawable", context.packageName)
        val painter: Painter = painterResource(id = drawableId)

        Image(
            painter = painter,
            contentDescription = photo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isExpanded) 250.dp else 150.dp)
                .padding(4.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )
    }
}