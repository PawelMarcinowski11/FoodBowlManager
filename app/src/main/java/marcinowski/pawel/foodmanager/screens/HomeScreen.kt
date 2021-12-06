package marcinowski.pawel.foodmanager.screens

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import marcinowski.pawel.foodmanager.*
import marcinowski.pawel.foodmanager.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {

    val productParameters = remember { ProductParameters() }

    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val productListState =  ( Products(context).getProducts().collectAsState(
        initial = null)
            )

    val productList = productListState

    val focusManager = LocalFocusManager.current

    BackHandler(enabled = !drawerState.isClosed) {
        coroutineScope.launch{
            focusManager.clearFocus()
            drawerState.close()
        }
    }

    BottomDrawer(
        gesturesEnabled = !drawerState.isClosed,
        drawerState = drawerState,
        drawerElevation = 20.dp,
        drawerShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        drawerContent = {
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 2.dp),
                onClick = {
                    coroutineScope.launch {
                        focusManager.clearFocus()
                        drawerState.close()
                    }
                },
                content = { Text( stringResource(R.string.drawer_button_hide) ) }
            )
            Column(modifier = Modifier.padding(bottom = 56.dp)) {
                InputsCard(
                    params = productParameters,
                    mode = Mode.Edit,
                    onClick = {

                        coroutineScope.launch {
                            try {
                                val parsedDate = LocalDate.parse(
                                    productParameters.expiryDate.value,
                                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                )

                                Products(context).updateProduct(
                                    productParameters.productName.value,
                                    productParameters.barcodeNumber.value,
                                    productParameters.id.value,
                                    parsedDate
                                )

                                Barcodes(context).updateBarcode(
                                    productParameters.productName.value,
                                    productParameters.barcodeNumber.value
                                )

                                focusManager.clearFocus()
                                drawerState.close()
                            }
                            catch (e: Exception) {
                                Toast.makeText(context, context.getResources().getString(R.string.toast_invalid_parameters), Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                )
                Box(Modifier.fillMaxHeight(0.45f))
            }
        },
        content = {
            ProductList(drawerState, productParameters, productList)
        }
    )
}


@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductList(
    drawerState: BottomDrawerState,
    productParameters: ProductParameters,
    productList: State<List<Product>?>
) {

    val state = rememberLazyListState()

    val coroutineScope= rememberCoroutineScope()

    val context = LocalContext.current

    //var previouslyRemoved = remember { Product("empty", "barcode", UUID.randomUUID()) }

    var previouslyRemoved: Product

    val snackState = remember { SnackbarHostState() }

    val snackScope = rememberCoroutineScope()

    Box (Modifier.fillMaxSize()) {
        Column (Modifier.fillMaxHeight()) {
            Card(
                shape = RectangleShape,
                modifier = Modifier
                    .height(52.dp)
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(),
                elevation = 4.dp
            ) {
                Box {
                    Text(
                        stringResource(R.string.header_product_list),
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                    )
                }
            }
            LazyColumn(
                state = state,
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .fillMaxHeight(1.0f)
                    .padding(horizontal = 24.dp)
            ) {
                state.layoutInfo
                items(items = productList.value ?: listOf(), key = { index -> index.id }) { item ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            true
                        }
                    )

                    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {

                        coroutineScope.launch {

                            previouslyRemoved = item
                            Products(context).removeProduct(item.id)

                            if (snackState.currentSnackbarData == null) {
                                snackScope.launch {
                                    val result = snackState
                                        .showSnackbar(
                                            message = context.resources.getString(R.string.snackbar_product_removal),
                                            actionLabel = context.resources.getString(R.string.snackbar_button_undo),
                                            duration = SnackbarDuration.Long
                                        )
                                    when (result) {
                                        SnackbarResult.ActionPerformed -> {
                                            Products(context).saveProduct(
                                                previouslyRemoved.name,
                                                previouslyRemoved.barcodeNumber,
                                                previouslyRemoved.expiryDate!!
                                            )
                                        }
                                        SnackbarResult.Dismissed -> {
                                        }
                                    }
                                }
                            }
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
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth(0.65f)
                                            .padding(start = 16.dp)
                                    ) {
                                        Text(
                                            item.name,
                                            color = MaterialTheme.colors.onBackground
                                        )
                                        Text(
                                            "Najlepiej spożyć przed: " + item.expiryDate?.format(
                                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                            ),
                                            color = MaterialTheme.colors.onSurface
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                productParameters.productName.value = item.name
                                                productParameters.barcodeNumber.value =
                                                    item.barcodeNumber
                                                productParameters.expiryDate.value =
                                                    item.expiryDate?.format(
                                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                                    ) ?: ""
                                                productParameters.id.value = item.id
                                                drawerState.expand()
                                            }
                                        }, modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            contentDescription = stringResource(R.string.accesibility_description_edit),
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
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.accesibility_description_delete),
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
        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .height(130.dp)) {
            SnackbarHost(
                hostState = snackState,
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp, start = 12.dp, end = 12.dp)
            )
        }
    }
}