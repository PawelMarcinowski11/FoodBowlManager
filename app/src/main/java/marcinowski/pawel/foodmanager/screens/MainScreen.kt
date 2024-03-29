package marcinowski.pawel.foodmanager.screens

import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import marcinowski.pawel.foodmanager.data_capturing.Camera

/**
 * Main screen, containing all three panels and the logic of navigating between them
 *
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(
    camera: Camera,
    pagerState: PagerState,
    darkTheme: MutableState<Boolean>
) {
    val previousRoute = rememberSaveable { mutableStateOf(1) }
    val currentRoute = rememberSaveable { mutableStateOf(1) }
    Scaffold(
        bottomBar = { BottomNavigationBar(pagerState, previousRoute, currentRoute, camera) },
        backgroundColor = MaterialTheme.colors.background
    ) {
        HorizontalPager(count = 3, state = pagerState) { page ->
            when (page) {
                0 -> {
                    ScanScreen(camera)
                }
                1 -> {
                    HomeScreen()
                }
                2 -> {
                    SettingsScreen(darkTheme)
                }
            }
        }
    }
}

/**
 * Bottom navigation bar, containing clickable icons of Scan screen, Home screen and Settings screen
 *
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavigationBar(
    pagerState: PagerState,
    previousRoute: MutableState<Int>,
    currentRoute: MutableState<Int>,
    camera: Camera
) {
    val items = listOf(
        NavigationItem.Scan,
        NavigationItem.Home,
        NavigationItem.Settings
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onBackground
    ) {
        if (currentRoute.value != pagerState.currentPage) {
            if (pagerState.currentPage == 0)
                camera.openCamera()
            else
                camera.closeCamera()
            previousRoute.value = currentRoute.value
        }
        currentRoute.value = pagerState.currentPage
        val scope = rememberCoroutineScope()
        BackHandler(enabled = currentRoute.value != 1) {
            scope.launch {
                if (previousRoute.value != currentRoute.value) {
                    pagerState.animateScrollToPage(previousRoute.value)
                    currentRoute.value = previousRoute.value
                } else
                    pagerState.animateScrollToPage(1)
            }
        }
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.title)
                    )
                },
                label = { Text(text = stringResource(id = item.title)) },
                alwaysShowLabel = true,
                selected = currentRoute.value == item.route,
                onClick = {
                    if (item.route != currentRoute.value) {
                        scope.launch {
                            pagerState.animateScrollToPage(item.route)
                        }
                    }
                }
            )
        }
    }
}