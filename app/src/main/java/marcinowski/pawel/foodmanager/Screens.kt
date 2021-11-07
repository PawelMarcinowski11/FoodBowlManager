package marcinowski.pawel.foodmanager

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

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
fun ScanScreen(textureViewRef: Reference<TextureView?>, camera: Camera) {
    HandleDisposal(camera)
    BackgroundCamera(textureViewRef, camera)
    Column() {
        InputsCard()
    }
}

@Composable
fun HandleDisposal (camera: Camera) {
    LaunchedEffect(Unit) {
    }
    DisposableEffect(Unit) {
        onDispose {
            camera.closeCamera();
        }
    }
}

@Composable
fun InputsCard() {
    Card(shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)) {
        Column(modifier = Modifier
            .padding(bottom = 10.dp, top = 6.dp)) {
            TopRow()
            MiddleRow()
            BottomRow()
        }
    }
}

@Composable
fun BackgroundCamera(textureViewRef: Reference<TextureView?>, camera: Camera) {
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

                textureViewRef.value = view.findViewById<View>(R.id.texture) as TextureView


                if (textureViewRef.value?.isAvailable == true) {
                    camera.openCamera()
                } else {
                    textureViewRef.value?.surfaceTextureListener = ImageProcessing(camera, textureViewRef).textureListener
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
fun TopRow() {
    Row(modifier = Modifier
        .wrapContentSize(Alignment.Center)) {

        var productName by remember { mutableStateOf("") }

        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.placeholder_product_name)) },
            textStyle = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(start = 9.dp, end = 2.dp)
                .height(54.dp)
                .fillMaxWidth(0.7f),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.secondary.copy(0.12f),
                unfocusedBorderColor = MaterialTheme.colors.secondary.copy(0f),
                )
        )
    }
}

@Composable
fun MiddleRow() {
    Row(modifier = Modifier
        .padding(top = 10.dp)
        .wrapContentSize(Alignment.Center)) {

        var barcodeNumber by remember { mutableStateOf("") }

        OutlinedTextField(value = barcodeNumber,
            onValueChange = { barcodeNumber = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.placeholder_barcode_number)) },
            textStyle = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(start = 10.dp, end = 2.dp)
                .height(54.dp)
                .fillMaxWidth(0.7f),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.secondary.copy(0.12f),
                unfocusedBorderColor = MaterialTheme.colors.secondary.copy(0f)
            )
        )

        Box(modifier = Modifier
            .fillMaxWidth()
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

@Composable
fun BottomRow() {
    Row(modifier = Modifier
        .padding(top = 10.dp)
        .wrapContentSize(Alignment.Center)) {

        var expiryDate by remember { mutableStateOf("") }

        OutlinedTextField(value = expiryDate,
            onValueChange = { expiryDate = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.placeholder_expiry_date)) },

            textStyle = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(start = 9.dp, end = 2.dp)
                .height(54.dp)
                .fillMaxWidth(0.7f),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.secondary.copy(0.12f),
                unfocusedBorderColor = MaterialTheme.colors.secondary.copy(0f)
            )
        )

        Box(modifier = Modifier
            .align(Alignment.CenterVertically)
            .fillMaxWidth()) {
            Text(stringResource(R.string.auto_switch),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(bottom = 1.dp, start = 10.dp))

            val isChecked = remember { mutableStateOf(false) }

            Switch(
                checked = isChecked.value,
                onCheckedChange = { checked ->
                    isChecked.value = checked
                },
                modifier = Modifier
                    .scale(1.0f)
                    .align(Alignment.CenterEnd)
                    .padding(top = 2.dp, bottom = 1.dp, start = 8.dp, end = 10.dp)
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