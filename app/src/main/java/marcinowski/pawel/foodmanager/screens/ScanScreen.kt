package marcinowski.pawel.foodmanager.screens

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import marcinowski.pawel.foodmanager.R
import marcinowski.pawel.foodmanager.data_capturing.Camera
import marcinowski.pawel.foodmanager.data_capturing.ImageProcessing
import marcinowski.pawel.foodmanager.data_capturing.lookUpProductName
import marcinowski.pawel.foodmanager.storage.Barcodes
import marcinowski.pawel.foodmanager.storage.Products
import marcinowski.pawel.foodmanager.utils.ProductParameters
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Mode {Add, Edit}

@Composable
fun ScanScreen(camera: Camera) {
    val productParameters = ProductParameters()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    BackgroundCamera(camera, productParameters)
    Column {
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
                    stringResource(R.string.header_scan),
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }
        }
        InputsCard(productParameters, Mode.Add) {
            coroutineScope.launch {
                if (productParameters.productName.value.trim() == "") {
                    Toast.makeText(context, context.resources.getString(R.string.toast_empty_product_name), Toast.LENGTH_SHORT).show()
                }
                else {
                    try {
                        val parsedDate: LocalDate

                        val year = productParameters.expiryDate.value.substringAfterLast('.')

                        parsedDate = if (year.length == 2) {
                            LocalDate.parse(
                                productParameters.expiryDate.value,
                                DateTimeFormatter.ofPattern("d.M.yy")
                            )
                        } else {
                            LocalDate.parse(
                                productParameters.expiryDate.value,
                                DateTimeFormatter.ofPattern("d.M.yyyy")
                            )
                        }

                        if (productParameters.barcodeNumber.value != "")
                            Barcodes(context).saveBarcode(
                                productParameters.productName.value,
                                productParameters.barcodeNumber.value
                            )
                        Products(context).saveProduct(
                            productParameters.productName.value,
                            productParameters.barcodeNumber.value,
                            parsedDate
                        )
                        Toast.makeText(context, context.resources.getString(R.string.toast_added_product), Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.toast_invalid_parameters),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

@Composable
fun InputsCard(params: ProductParameters, mode: Mode, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
        elevation = 0.dp
    ) {
        Row (
            modifier = Modifier
            .padding(bottom = 10.dp, top = 6.dp)
        ) {
            Column {
                NameField(params)
                BarcodeField(params)
                ExpiryDateField(params)
            }
            Column(
                modifier = Modifier.align(Alignment.Bottom)
            ) {
                AutoCompleteSwitch(params)
                SaveButton(mode, onClick)
            }
        }
    }
}

@Composable
private fun BackgroundCamera(
    camera: Camera,
    params: ProductParameters
) {
    Box{
        Box(
            modifier = Modifier
                .fillMaxSize()
        )
        Text(
            text = stringResource(R.string.reminder_access_camera),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 16.dp, end = 16.dp)
        )
        AndroidView(
            factory = { context: Context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.camera_view, null, false)
                val textureView = view.findViewById<View>(R.id.texture) as TextureView
                if (!textureView.isAvailable) {
                    camera.textureViewRef = textureView
                    textureView.surfaceTextureListener = ImageProcessing(textureView, params, context).textureListener
                }
                view
            },
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(top = 60.dp)
                .aspectRatio(9 / 16f)
        )
    }
}

@Composable
private fun NameField(productParameters: ProductParameters) {
    SlimTextField(
        value = productParameters.productName.value,
        onValueChange = { productParameters.productName.value = it},
        modifier = Modifier
            .padding(start = 9.dp, end = 2.dp)
            .height(40.dp)
            .fillMaxWidth(0.7f)
            .background(
                MaterialTheme.colors.secondary.copy(0.08f),
                MaterialTheme.shapes.small,
            ),
        placeholderText = stringResource(R.string.placeholder_product_name)
    )
}

@Composable
private fun BarcodeField(productParameters: ProductParameters) {
    val context = LocalContext.current

    val barcodesListState = (Barcodes(context).getBarcodes().collectAsState(
        initial = null)
            )

    SlimTextField(
        value = productParameters.barcodeNumber.value,
        onValueChange = {
            val barcodeFormat = """\A\d{0,13}\Z"""
            if (Regex(barcodeFormat).matches(it)) {
                productParameters.barcodeNumber.value = it
                if (productParameters.autoComplete.value) {
                    val newNumber = it
                    val existingBarcode = barcodesListState.value?.find {it.number == newNumber}
                    if (existingBarcode != null) {
                        productParameters.productName.value = existingBarcode.name
                    }
                    else if (it.length == 6 || it.length == 8 || it.length == 12 || it.length == 13) {
                        lookUpProductName(
                            productParameters.barcodeNumber.value,
                            productParameters.productName,
                            context
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .padding(top = 10.dp, start = 9.dp, end = 2.dp)
            .height(40.dp)
            .fillMaxWidth(0.7f)
            .background(
                MaterialTheme.colors.secondary.copy(0.08f),
                MaterialTheme.shapes.small,
            ),
        placeholderText = stringResource(R.string.placeholder_barcode_number),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
private fun AutoCompleteSwitch(productParameters: ProductParameters) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)) {
        Text(stringResource(R.string.switch_auto_complete),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(bottom = 2.dp, start = 10.dp))

        Switch(
            checked = productParameters.autoComplete.value,
            onCheckedChange = { checked ->
                productParameters.autoComplete.value = checked
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterEnd)
                .padding(start = 8.dp, end = 10.dp)
        )
    }
}


@Composable
private fun ExpiryDateField(productParameters: ProductParameters) {
    SlimTextField(
        value = productParameters.expiryDate.value,
        onValueChange = {
            val dateFormat = """\A\d{0,2}\Z|\A\d{0,2}[.]{1}\d{0,2}\Z|\A\d{0,2}[.]{1}\d{0,2}[.]{1}\d{0,4}\Z"""
            if (Regex(dateFormat).matches(it))
                productParameters.expiryDate.value = it
        },
        modifier = Modifier
            .padding(top = 10.dp, start = 9.dp, end = 2.dp)
            .height(40.dp)
            .fillMaxWidth(0.7f)
            .background(
                MaterialTheme.colors.secondary.copy(0.08f),
                MaterialTheme.shapes.small,
            ),
        placeholderText = stringResource(R.string.placeholder_expiry_date),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
fun SaveButton(mode: Mode, onClickEvent: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
    ) {
        Button(
            onClick = { onClickEvent() },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 9.dp, end = 12.dp)
                .fillMaxWidth(),
        ) {
            Text(
                if (mode == Mode.Add)
                    stringResource(R.string.button_save_new)
                else stringResource(R.string.button_save_edited),
                modifier = Modifier.padding(bottom = 1.dp)
            )
        }
    }
}

@Composable
fun SlimTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholderText: String,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    BasicTextField(modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.padding(horizontal = 16.dp)) {
                    if (value.isEmpty()) Text(
                        placeholderText,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}
