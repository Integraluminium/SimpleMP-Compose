package com.lighttigerxiv.simple.mp.compose.screens.main.playlists

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TabRow
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.lighttigerxiv.simple.mp.compose.R
import com.lighttigerxiv.simple.mp.compose.ui.composables.CustomTextField
import com.lighttigerxiv.simple.mp.compose.ui.composables.ImageCard
import com.lighttigerxiv.simple.mp.compose.activities.main.MainVM
import com.lighttigerxiv.simple.mp.compose.data.variables.ImageSizes
import com.lighttigerxiv.simple.mp.compose.data.variables.SCREEN_PADDING
import com.lighttigerxiv.simple.mp.compose.data.variables.SMALL_SPACING
import com.lighttigerxiv.simple.mp.compose.ui.composables.SheetDraggingBar
import com.lighttigerxiv.simple.mp.compose.ui.composables.spacers.MediumVerticalSpacer
import com.lighttigerxiv.simple.mp.compose.functions.getAppString
import com.lighttigerxiv.simple.mp.compose.functions.getImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistsScreen(
    mainVM: MainVM,
    vm: PlaylistsScreenVM,
    activityContext: ViewModelStoreOwner,
    navController: NavHostController
) {

    val surfaceColor = mainVM.surfaceColor.collectAsState().value

    val screenLoaded = vm.screenLoaded.collectAsState().value

    val genres = vm.genres.collectAsState().value

    val playlists = vm.currentPlaylists.collectAsState().value

    val searchText = vm.searchText.collectAsState().value

    val playlistNameText = vm.playlistNameText.collectAsState().value

    val context = LocalContext.current

    val configuration = LocalConfiguration.current

    val pagerState = rememberPagerState(2)

    val createPlaylistsScaffoldState = rememberBottomSheetScaffoldState()

    val scope = rememberCoroutineScope()

    val userPlaylistsGridState = rememberLazyGridState()

    val gridCellsCount = when (configuration.orientation) {

        Configuration.ORIENTATION_PORTRAIT -> 2
        else -> 4
    }


    if (!screenLoaded) {
        vm.loadScreen(mainVM)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TabRow(
                modifier = Modifier
                    .padding(
                        top = SCREEN_PADDING,
                        start = SCREEN_PADDING,
                        end = SCREEN_PADDING
                    ),
                selectedTabIndex = pagerState.currentPage,
                contentColor = mainVM.surfaceColor.collectAsState().value,
                indicator = {},
            ) {

                val genrePlaylistColor = when (pagerState.currentPage) {

                    0 -> MaterialTheme.colorScheme.surfaceVariant
                    else -> mainVM.surfaceColor.collectAsState().value
                }

                val yourPlaylistsColor = when (pagerState.currentPage) {

                    1 -> MaterialTheme.colorScheme.surfaceVariant
                    else -> mainVM.surfaceColor.collectAsState().value
                }

                Tab(
                    text = {
                        Text(
                            text = remember { getAppString(context, R.string.Genres) },
                            fontSize = 16.sp
                        )
                    },
                    selected = pagerState.currentPage == 0,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                    modifier = Modifier
                        .background(mainVM.surfaceColor.collectAsState().value)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(percent = 100))
                        .background(genrePlaylistColor)
                )
                Tab(
                    text = { Text(remember { getAppString(context, R.string.YourPlaylists) }, fontSize = 16.sp) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    selected = pagerState.currentPage == 1,
                    onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                    modifier = Modifier
                        .background(mainVM.surfaceColor.collectAsState().value)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(percent = 100))
                        .background(yourPlaylistsColor)
                )
            }

            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
            ) { currentPage ->


                when (currentPage) {

                    //************************************************
                    // Genre Playlists Screen
                    //************************************************

                    0 -> {

                        if (screenLoaded) {
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(SCREEN_PADDING),
                                columns = GridCells.Fixed(gridCellsCount),
                                verticalArrangement = Arrangement.spacedBy(SMALL_SPACING),
                                horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING)
                            ) {

                                items(
                                    items = genres!!,
                                    key = { genre -> genre },
                                ) {genre ->

                                    ImageCard(
                                        cardImage = remember { getImage(context, R.drawable.playlist_filled, ImageSizes.MEDIUM) },
                                        imageTint = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                        cardText = remember { genre },
                                        onCardClicked = {
                                            vm.openGenrePlaylist(navController, genre)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    //************************************************
                    // Playlists Screen
                    //************************************************

                    1 -> {

                        if (screenLoaded) {

                            BottomSheetScaffold(
                                scaffoldState = createPlaylistsScaffoldState,
                                sheetShape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                                sheetPeekHeight = 0.dp,
                                sheetContent = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .background(MaterialTheme.colorScheme.background)
                                            .padding(SMALL_SPACING)

                                    ) {

                                        SheetDraggingBar()

                                        MediumVerticalSpacer()

                                        Text(
                                            text = remember { getAppString(context, R.string.PlaylistName) },
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        CustomTextField(
                                            text = playlistNameText,
                                            placeholder = remember { getAppString(context, R.string.InsertPlaylistName) },
                                            onTextChange = {
                                                vm.updatePlaylistNameText(it)
                                            },
                                            textType = "text"
                                        )

                                        MediumVerticalSpacer()

                                        Row(
                                            horizontalArrangement = Arrangement.End,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = {

                                                    scope.launch {

                                                        vm.createPlaylist()

                                                        vm.updatePlaylistNameText("")

                                                        createPlaylistsScaffoldState.bottomSheetState.collapse()
                                                    }
                                                },
                                                enabled = playlistNameText.isNotEmpty()
                                            ) {

                                                Text(
                                                    text = remember { getAppString(context, R.string.Create) }
                                                )
                                            }
                                        }
                                    }
                                },

                                ) { sheetPadding ->

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(mainVM.surfaceColor.collectAsState().value)
                                        .padding(sheetPadding)
                                        .padding(SCREEN_PADDING)
                                ) {

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {

                                        CustomTextField(
                                            text = searchText,
                                            onTextChange = {

                                                vm.updateSearchText(it)

                                                vm.filterPlaylists()

                                                scope.launch {
                                                    userPlaylistsGridState.scrollToItem(0)
                                                }
                                            },
                                            placeholder = remember { getAppString(context, R.string.SearchPlaylists) },
                                            sideIcon = R.drawable.plus,
                                            onSideIconClick = {
                                                scope.launch {
                                                    createPlaylistsScaffoldState.bottomSheetState.expand()
                                                }
                                            }
                                        )
                                    }

                                    MediumVerticalSpacer()

                                    LazyVerticalGrid(
                                        state = userPlaylistsGridState,
                                        columns = GridCells.Fixed(gridCellsCount),
                                        verticalArrangement = Arrangement.spacedBy(SMALL_SPACING),
                                        horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING),
                                        content = {

                                            items(playlists!!) { playlist ->

                                                val playlistImage = if (playlist.image.isNullOrEmpty()) {
                                                    getImage(context, R.drawable.playlist_filled, ImageSizes.MEDIUM)
                                                } else {
                                                    val imageBytes = Base64.decode(playlist.image, Base64.DEFAULT)
                                                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                                }


                                                ImageCard(
                                                    modifier = Modifier.animateItemPlacement(),
                                                    cardImage = playlistImage,
                                                    imageTint = if (playlist.image.isNullOrEmpty()) ColorFilter.tint(MaterialTheme.colorScheme.primary) else null,
                                                    cardText = playlist.name,
                                                    onCardClicked = {

                                                        vm.openPlaylist(activityContext, navController, playlist._id.toHexString())
                                                    }
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}