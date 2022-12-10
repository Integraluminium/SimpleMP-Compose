package com.lighttigerxiv.simple.mp.compose.composables

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lighttigerxiv.simple.mp.compose.R
import com.lighttigerxiv.simple.mp.compose.Song

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    songAlbumArt: Bitmap?,
    highlight: Boolean = false,
    popupMenuEntries: ArrayList<String> = ArrayList(),
    onSongClick: () -> Unit = {},
    onMenuClicked: (option: String) -> Unit = {}
) {

    val context = LocalContext.current
    val songTitle = remember { song.title }
    val songArtist = remember { song.artistName }
    val isPopupMenuExpanded = remember { mutableStateOf(false) }

    val titleColor = when (highlight) {
        true -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }


    val titleWeight = when (highlight){
        true -> FontWeight.Medium
        else -> FontWeight.Normal
    }


    Row(modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .clip(RoundedCornerShape(14.dp))
        .combinedClickable(
            onClick = {onSongClick()},
            onLongClick = {isPopupMenuExpanded.value = true}
        )
    ) {

        AsyncImage(
            model = remember{ songAlbumArt ?: BitmapFactory.decodeResource(context.resources, R.drawable.icon_music_record) },
            contentDescription = "",
            colorFilter = if(songAlbumArt == null) ColorFilter.tint(MaterialTheme.colorScheme.primary) else null,
            modifier = Modifier
                .clip(RoundedCornerShape(20))
                .height(70.dp)
                .width(70.dp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(10.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = songTitle,
                fontSize = 16.sp,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = titleWeight
            )

            Text(
                text = songArtist,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }


        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
        ) {

            DropdownMenu(
                expanded = isPopupMenuExpanded.value,
                onDismissRequest = { isPopupMenuExpanded.value = false }
            ) {

                if (popupMenuEntries.contains("artist")) {

                    DropdownMenuItem(
                        text = { Text(text = "Go to Artist") },
                        onClick = { onMenuClicked("artist") }
                    )
                }

                if (popupMenuEntries.contains("album")) {

                    DropdownMenuItem(
                        text = { Text(text = "Go to Album") },
                        onClick = { onMenuClicked("album") }
                    )
                }

                if (popupMenuEntries.contains("playlist")) {

                    DropdownMenuItem(
                        text = { Text(text = "Add to Playlist") },
                        onClick = { onMenuClicked("playlist") }
                    )
                }
            }
        }
    }
}

