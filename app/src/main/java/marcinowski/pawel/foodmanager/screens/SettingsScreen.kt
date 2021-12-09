package marcinowski.pawel.foodmanager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import marcinowski.pawel.foodmanager.R

@Composable
fun SettingsScreen() {
    Column (Modifier.fillMaxHeight()) {
        Card(
            shape = RectangleShape,
            modifier = Modifier
                .height(52.dp)
                .padding(bottom = 4.dp)
                .fillMaxWidth(),
            elevation = 4.dp,
        ) {
            Box {
                Text(
                    stringResource(R.string.header_settings),
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }
        }
        Text(
            text = stringResource(R.string.header_settings)
        )
    }
}
