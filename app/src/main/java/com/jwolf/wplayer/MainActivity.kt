package com.jwolf.wplayer

import android.os.Bundle
import android.provider.MediaStore
import android.text.style.BackgroundColorSpan
import android.text.style.LineBackgroundSpan
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwolf.wplayer.ui.theme.WPlayerTheme
import org.intellij.lang.annotations.JdkConstants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    WPlayerTheme {
        MainScreen()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var scaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current
    val projection = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media._ID
    )
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )
    var musicList = mutableListOf<MusicPlaceHolder>()
    cursor?.use {
        var titleIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        var artistIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        var idIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        while (it.moveToNext()) {
            var title = it.getString(titleIdx)
            var artist = it.getString(artistIdx)
            var id = it.getInt(idIdx)
            musicList.add(
                MusicPlaceHolder(
                    id = id.toInt(),
                    title = title,
                    artist = artist,
                    albumimg = R.drawable.img_album_generic_3
                )
            )
        }
    }
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 300.dp,
            sheetContent = {
                MediaControl()
            },
            topBar = { TabRow(0) { LeadingIcoinTab() } }
        ) { innerPadding ->
            MusicList(
                modifier = Modifier.padding(innerPadding),
                data = musicList
            )
        }
    }


@Composable
fun LeadingIcoinTab(modifier: Modifier = Modifier.padding(50.dp)) {
    Text(text = "Leading Icoin", modifier = modifier)
}

// Formato do item
@Composable
fun MusicListItens(modifier: Modifier = Modifier.padding(all = 16.dp), data: MusicPlaceHolder) {
    Row (modifier = modifier
        .padding(start = 7.dp, end = 32.dp)
        .fillMaxWidth()
        .size(90.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            painter = painterResource(id = data.albumimg),
            contentDescription = "album cover"
        )
        Column (modifier = Modifier.padding(all = 15.dp)){
            Text(text = data.title)
            Text(text = data.artist)
        }
    }
}
//Lista de itens
@Composable
fun MusicList (modifier: Modifier = Modifier.padding(all = 16.dp), data: List<MusicPlaceHolder>){
    LazyColumn(modifier = modifier) {
        items(data){
            music -> MusicListItens(data = music)
        }
    }
}

@Composable
fun PlayButton(modifier: Modifier = Modifier.size(90.dp)) {
    Icon(Icons.Filled.PlayArrow, contentDescription = "Play", modifier = modifier)
}
fun NextButton(modifier: Modifier = Modifier.size(90.dp)) {
    Icon(R.drawable.outline_play_circle_24, contentDescription = "Next", modifier = modifier)
}

@Composable
fun MediaControl(modifier: Modifier = Modifier.fillMaxSize()) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            PlayButton()
            NextButton()
        }
    }
}
