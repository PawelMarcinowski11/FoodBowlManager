package marcinowski.pawel.foodmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

data class ProductParameters (
    var productName: MutableState<String> = mutableStateOf(""),
    var barcodeNumber: String = "",
    var expiryDate: String = ""
)

@Composable
fun HomeScreen() {
    Scaffold(modifier = Modifier.background(color= Color.Gray)) {
        Text(
            text = "Tu będzie lista produktów",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ScanScreen(camera: Camera) {

    var productParameters = ProductParameters()

    BackgroundCamera(camera, productParameters)
    Column() {
        InputsCard(productParameters)
    }
}

@Composable
private fun InputsCard(params: ProductParameters) {
    Card(shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)) {
        Column(modifier = Modifier
            .padding(bottom = 10.dp, top = 6.dp)) {
            TopRow(params)
            MiddleRow(params)
            BottomRow(params)
        }
    }
}

@Composable
private fun BackgroundCamera(
    camera: Camera,
    params: ProductParameters
) {
    Box() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        )
        Text(
            text = stringResource(R.string.camera_access_reminder),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 16.dp, end = 16.dp)
        )
        AndroidView(
            factory = { context: Context ->
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.camera_view, null, false)

                var textureView = view.findViewById<View>(R.id.texture) as TextureView

                if (textureView.isAvailable == false) {
                    camera.textureViewRef = textureView
                    textureView.surfaceTextureListener = ImageProcessing(textureView, params).textureListener
                }

                // do whatever you want...
                view // return the view
            },// update  = { view -> textureViewRef.value = view.findViewById<View>(R.id.texture) as TextureView}
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(top = 60.dp)
                .aspectRatio(9 / 16f)
        )
    }
}

@Composable
private fun TopRow(productParameters: ProductParameters) {
    Row(modifier = Modifier
        .wrapContentSize(Alignment.Center)) {


        SlimTextField(
            value = productParameters.productName.value,
            onValueChange = { productParameters.productName.value = it },
            textStyle = TextStyle(fontWeight = FontWeight.Bold),
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
}

@Composable
private fun MiddleRow(productParameters: ProductParameters) {
    Row(modifier = Modifier
        .padding(top = 10.dp)
        .wrapContentSize(Alignment.Center)) {

        SlimTextField(
            value = productParameters.barcodeNumber,
            onValueChange = { productParameters.barcodeNumber = it },
            textStyle = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(start = 9.dp, end = 2.dp)
                .height(40.dp)
                .fillMaxWidth(0.7f)
                .background(
                    MaterialTheme.colors.secondary.copy(0.08f),
                    MaterialTheme.shapes.small,
                ),
            placeholderText = stringResource(R.string.placeholder_barcode_number)
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .align(Alignment.CenterVertically)) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 9.dp, end = 12.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    stringResource(R.string.save_new_button),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
    }
}

fun xd(x : Reference<String>) {
    x.value = "lol"
}


@Composable
private fun BottomRow(productParameters: ProductParameters) {
    Row(modifier = Modifier
        .padding(top = 10.dp)
        .wrapContentSize(Alignment.Center)) {




        SlimTextField(
            value = productParameters.expiryDate,
            onValueChange = { productParameters.expiryDate = it },
            textStyle = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(start = 9.dp, end = 2.dp)
                .height(40.dp)
                .fillMaxWidth(0.7f)
                .background(
                    MaterialTheme.colors.secondary.copy(0.08f),
                    MaterialTheme.shapes.small,
                ),
            placeholderText = stringResource(R.string.placeholder_expiry_date)
        )

        Box(modifier = Modifier
            .align(Alignment.CenterVertically)
            .fillMaxWidth()) {
            Text(stringResource(R.string.auto_switch),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(bottom = 2.dp, start = 10.dp))

            val isChecked = remember { mutableStateOf(false) }

            Switch(
                checked = isChecked.value,
                onCheckedChange = { checked ->
                    isChecked.value = checked
                },
                modifier = Modifier
                    .height(40.dp)
                    .scale(1.0f)
                    .align(Alignment.CenterEnd)
                    .padding(start = 8.dp, end = 10.dp)
            )
        }
    }
}

@Composable
fun SettingsScreen() {
    Text(
        text = "Ustawienia"
    )
}




@Composable
private fun SlimTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    placeholderText: String,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    BasicTextField(modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = textStyle,
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
