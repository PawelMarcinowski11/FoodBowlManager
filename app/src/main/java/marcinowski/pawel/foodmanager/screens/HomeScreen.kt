package marcinowski.pawel.foodmanager.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import marcinowski.pawel.foodmanager.ProductEntry
import marcinowski.pawel.foodmanager.ProductParameters

//private var isDeleting: Semaphore = Semaphore(1)

val productParameters = ProductParameters()

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {
    //Scaffold(modifier = Modifier.background(color = Color.Gray)) {

    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = !drawerState.isClosed) {
        coroutineScope.launch{
            drawerState.close()
        }
    }

    BottomDrawer(
        gesturesEnabled = !drawerState.isClosed,
        drawerState = drawerState,
        drawerElevation = 20.dp,
        drawerContent = {
            Button(
                modifier = Modifier.align( Alignment.CenterHorizontally ).padding( top = 16.dp ),
                onClick = { coroutineScope.launch { drawerState.close() } },
                content = { Text( "Powrót" ) }
            )
            Column(modifier = Modifier.padding(bottom = 56.dp)) {
                InputsCard(params = productParameters, mode = Mode.Edit)
                Box(Modifier.fillMaxHeight(0.45f))
            }
        },
        content = {
            ProductList(drawerState, productParameters)
        }
    )
}


@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductList(drawerState: BottomDrawerState, productParameters: ProductParameters) {

    val ItemMockup = mutableStateListOf(
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

    val coroutineScope= rememberCoroutineScope()




    LazyColumn(
        state = state,
        modifier = Modifier
            .animateContentSize()
            .fillMaxSize()
            .padding(bottom = 56.dp, start = 24.dp, end = 24.dp)
    ) {
        state.layoutInfo
        items(items = productList, key = { index -> index.id }) { item ->
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    true
                }
            )

            if (dismissState.isDismissed(DismissDirection.StartToEnd)) {

                coroutineScope.launch {
                    productList.remove(item)
                }

            }


            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.padding(vertical = 8.dp),
                directions = setOf(DismissDirection.StartToEnd),
                dismissThresholds = { direction ->
                    FractionalThreshold(0.65f)
                },
                background = {
                },
                dismissContent = {
                    Card(
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier,
                        elevation = 4.dp
                    ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .padding(start = 16.dp)) {
                                    Text(
                                        item.name,
                                        color = MaterialTheme.colors.onBackground
                                    )
                                    Text("Najlepiej spożyć przed: 01.12.2023",
                                        color = MaterialTheme.colors.onSurface)
                                }

                                IconButton(onClick = { coroutineScope.launch{
                                    productParameters.productName.value = item.name
                                    drawerState.expand() } }, modifier = Modifier
                                    .align(Alignment.CenterVertically)) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Localized description",
                                        tint = MaterialTheme.colors.onBackground,
                                        modifier = Modifier
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            dismissState.dismiss(
                                                DismissDirection.StartToEnd
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Localized description",
                                        tint = MaterialTheme.colors.onBackground
                                    )
                                }
                            }


                    }
                }
            )
        }
    }
}