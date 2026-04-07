package com.jwolf.wplayer

import android.content.res.Resources
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Slider
import com.composeunstyled.TabGroup
import com.composeunstyled.Thumb
import com.composeunstyled.rememberSliderState
import com.composeunstyled.rememberTabGroupState
import com.jwolf.wplayer.ui.theme.DarkPurple
import com.jwolf.wplayer.ui.theme.Purple982
import com.jwolf.wplayer.ui.theme.PurpleE5
import com.jwolf.wplayer.ui.theme.PurpleGrey80
import com.jwolf.wplayer.ui.theme.WPlayerTheme


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
    var selected by remember { mutableStateOf(0) }

    var scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )
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
        sheetPeekHeight = 50.dp,
        sheetMaxWidth = BottomSheetDefaults.SheetMaxWidth,
        sheetContent = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MediaControl()
            }
        },
        topBar = {
            PrimaryTabRow(selected){
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Tab(
                        selected = selected == 0,
                        onClick = { selected = 0 },
                        enabled = true,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Text(
                            text = "Songs",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Tab(
                        selected = selected == 1,
                        onClick = { selected = 1 },
                        enabled = true,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.secondary,
                    ) {
                        Text(
                            text = "PlayList",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when (selected) {
            0 -> MusicList(data = musicList)
            1 -> Testing()
        }
    }
}
// Formato do item
@Composable
fun MusicListItens(modifier: Modifier = Modifier, data: MusicPlaceHolder) {
    Row (modifier = modifier
        .fillMaxWidth(1f)
        .size(70.dp)
        .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            painter = painterResource(id = data.albumimg),
            contentDescription = "album cover",
            modifier = Modifier
                .padding(top = 6.dp, bottom = 6.dp, end = 5.dp)
                .graphicsLayer(scaleX = 0.9f)
                .clip(RoundedCornerShape(19.dp))
                .border(2.dp, PurpleGrey80, RoundedCornerShape(19.dp))
        )
        Column (modifier = Modifier.fillMaxWidth()){
            Text(text = data.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = modifier
                    .basicMarquee()
            )
            Text(text = "by " + data.artist,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = modifier
                    .basicMarquee()
                    .padding(start = 13.dp)
            )
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
fun Testing(modifier: Modifier = Modifier){
    Text(text = "JubJub")
}

@Composable
fun PlayButton(onClick: () -> Unit = {},
               modifier: Modifier = Modifier.size(110.dp)) {
    Icon(Icons.Rounded.PlayCircle,
        contentDescription = "Play",
        modifier = modifier,
        tint = Purple982)
}

@Composable
fun NextButton(modifier: Modifier = Modifier.size(75.dp)) {
    Icon(Icons.Rounded.SkipNext,
        contentDescription = "SkipNext",
        modifier = modifier,
        tint = DarkPurple)
}

@Composable
fun PreviousButton(modifier: Modifier = Modifier.size(75.dp)) {
    Icon(Icons.Rounded.SkipPrevious,
        contentDescription = "SkipPrevious",
        modifier = modifier,
        tint = DarkPurple)
}
@Composable
fun Slider(modifier: Modifier = Modifier.fillMaxWidth(0.78f)) {
    val sliderState = rememberSliderState(initialValue = 0f)
    Slider(
        state = sliderState,
        track = {
            Box(
                modifier = modifier
                    .width(100.dp)
                    .height(8.dp)
                    .background(PurpleGrey80, CircleShape)
            )
        },
        thumb = {
            Box {
                Thumb(
                    shape = CircleShape,
                    color = Purple982,
                )
            }
        }
    )
}

@Composable
fun MediaControl(modifier: Modifier = Modifier.wrapContentSize()) {
    Column(modifier = modifier.height(75.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Row (verticalAlignment = Alignment.Top) {
            Slider()
        }
    }
    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            PreviousButton()
            PlayButton()
            NextButton()
        }
    }
}
