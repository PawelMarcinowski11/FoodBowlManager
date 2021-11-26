package marcinowski.pawel.foodmanager.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import java.util.*

private var isDeleting: Semaphore = Semaphore(1)

@Composable
fun HomeScreen() {
    Scaffold(modifier = Modifier.background(color = Color.Gray)) {
        ProductList()
    }
}


data class ProductEntry(
    val name: String,
    val id: Number,
    val expiryDate: Date? = null
)


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductList() {

    val ItemMockup = arrayListOf(
        ProductEntry("margarita0", 0),
        ProductEntry("capriciossa1", id = 1),
        ProductEntry("margarita2", 2),
        ProductEntry("capriciossa3", id = 3),
        ProductEntry("margarit4a", 4),
        ProductEntry("capric5iossa", id = 5),
        ProductEntry("6margarita", 6),
        ProductEntry("caprici7ossa", id = 7),
        ProductEntry("m8argarita", 8),
        ProductEntry("caprici9ossa", id = 9),
        ProductEntry("margarita", 10),
        ProductEntry("capriciossa", id = 11),
        ProductEntry("margarita", 12),
        ProductEntry("capriciossa", id = 13),
        ProductEntry("margarita", 14),
        ProductEntry("capriciossa", id = 15),
        ProductEntry("margarita", 16),
        ProductEntry("capriciossa", id = 17),
        ProductEntry("margarita", 18),
        ProductEntry("capriciossa", id = 19),
        ProductEntry("margarita", 20),
        ProductEntry("capriciossa", id = 21),
        ProductEntry("quattro carni", id = 22)
    )

    val productList = ItemMockup

    val state = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = state,
        modifier = Modifier
            .animateContentSize()
            .fillMaxSize()
            .padding(bottom = 56.dp, start = 16.dp, end = 16.dp)
    ) {
        state.layoutInfo
        items(items = productList) { item ->
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    //ItemMockup.remove(item)
                    //ItemMockup.value =  MutableLiveData <MutableList<ProductEntry>>(mutableListOf(ProductEntry("margarita", 0), ProductEntry("hawajska", id = 17), ProductEntry("quattro carni", id = 83))).value

                    if (it != DismissValue.Default) {
                    }

                    //state.layoutInfo

                    //false
                    //if (it == DismissValue.DismissedToEnd) unread = !unread
                    true
                }
            )

            //Log.i("xdd",item.name)

//            if (dismissState.targetValue == DismissValue.DismissedToEnd) {
            if (dismissState.isDismissed(DismissDirection.StartToEnd)) {

                coroutineScope.launch {
                    if (isDeleting.tryAcquire()) {
                        //    delay(100L)
                        try {
                            dismissState.reset()
                            productList.remove(item)
                            state.layoutInfo
                        } catch (e: Exception) {
                        } finally {
                            isDeleting.release()
                        }
                    }
                }

            }

            if (true) {
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier.padding(vertical = 8.dp),
                    directions = setOf(DismissDirection.StartToEnd),
                    dismissThresholds = { direction ->
                        FractionalThreshold(0.65f)
                    },
                    background = {
                        val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> Color.LightGray
                                DismissValue.DismissedToEnd -> MaterialTheme.colors.secondary
                                DismissValue.DismissedToStart -> Color.Red
                            }
                        )
                        val textColor by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> MaterialTheme.colors.onBackground
                                DismissValue.DismissedToEnd -> MaterialTheme.colors.background
                                DismissValue.DismissedToStart -> Color.Red
                            }
                        )
                        val icon = when (direction) {
                            DismissDirection.StartToEnd -> Icons.Filled.RestaurantMenu
                            DismissDirection.EndToStart -> Icons.Default.Delete
                        }
                        val warningText = when (dismissState.targetValue) {
                            DismissValue.Default -> "Przewiniecie usunie pizze"
                            DismissValue.DismissedToEnd -> "Usunales pizze, jak mogles"
                            DismissValue.DismissedToStart -> "Usunales pizze, jak mogles"
                        }
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = warningText,
                                color = textColor
                            )
                            Icon(
                                icon,
                                contentDescription = "Localized description",
                                tint = MaterialTheme.colors.background,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                            )
                        }
                    },
                    dismissContent = {
                        val color by animateColorAsState(
                            when ( dismissState.dismissDirection) {
                                DismissDirection.StartToEnd -> Color.White
                                DismissDirection.EndToStart -> Color.White
                                null -> Color.White
                            }

                        )
                        Card(
                            modifier = Modifier,
                            elevation = 4.dp,
                            backgroundColor = color
                        ) {
                            //state.layoutInfo
                            ListItem(
                                text = {
                                    Text(
                                        item.name,
                                        color = MaterialTheme.colors.onBackground
                                    )
                                },
                                secondaryText = { Text("Przewin aby usumac pizze") }
                            )
                        }
                    }
                )
            }
        }
    }
}