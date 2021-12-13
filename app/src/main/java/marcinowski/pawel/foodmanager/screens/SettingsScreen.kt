package marcinowski.pawel.foodmanager.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import marcinowski.pawel.foodmanager.R

@Composable
fun SettingsScreen() {
    Column (Modifier.fillMaxHeight().fillMaxWidth()) {
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
        Column (modifier = Modifier.fillMaxWidth()) {
            NotificationsSettings()
            Divider(thickness = 1.dp)
            ThemeSettings()
            Divider(thickness = 1.dp)
        }
    }
}

@Composable
private fun NotificationsSettings() {
    val useNotifications = remember { mutableStateOf(true) }
    SectionHeader(stringResource(R.string.section_notifications))
    NotificationsSwitch(useNotifications)
    Divider(thickness = 1.dp)
    SectionHeader(stringResource(R.string.section_notification_types))
    ShortDateNotificationsSwitch(useNotifications)
    DailyNotificationsSwitch(useNotifications)
}

@Composable
private fun ThemeSettings() {
    val useSystemTheme = remember { mutableStateOf(true) }
    SectionHeader(stringResource(R.string.section_themes))
    SystemThemeSwitch(useSystemTheme)
    ApplicationThemeSwitch(useSystemTheme)
}

@Composable
private fun NotificationsSwitch(useNotifications: MutableState<Boolean>) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_notifications),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
        )
        Switch(
            checked = useNotifications.value,
            onCheckedChange = { checked ->
                useNotifications.value = checked
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 10.dp)
        )
    }
}


@Composable
private fun ShortDateNotificationsSwitch(useNotifications: MutableState<Boolean>) {
    val notifyOnShortDate = remember { mutableStateOf(true) }
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_short_date_notifications),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
        )
        Switch(
            enabled = useNotifications.value,
            checked = notifyOnShortDate.value,
            onCheckedChange = { checked ->
                notifyOnShortDate.value = checked
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 10.dp)
        )
    }
}

@Composable
private fun DailyNotificationsSwitch(useNotifications: MutableState<Boolean>) {
    val notifyDaily = remember { mutableStateOf(true) }
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_daily_notifications),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
        )
        Switch(
            enabled = useNotifications.value,
            checked = notifyDaily.value,
            onCheckedChange = { checked ->
                notifyDaily.value = checked
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 10.dp)
        )
    }
}

@Composable
private fun SystemThemeSwitch(useSystemTheme: MutableState<Boolean>) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_use_system_theme),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
        )
        Switch(
            checked = useSystemTheme.value,
            onCheckedChange = { checked ->
                useSystemTheme.value = checked
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 10.dp)
        )
    }
}

@Composable
private fun ApplicationThemeSwitch(useSystemTheme: MutableState<Boolean>) {
    val useDarkTheme = remember { mutableStateOf(true) }
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_use_dark_theme),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
        )
        Switch(
            enabled = !useSystemTheme.value,
            checked = useDarkTheme.value,
            onCheckedChange = { checked ->
                useDarkTheme.value = checked
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 10.dp)
        )
    }
}

@Composable
private fun SectionHeader(headerText: String) {
    Text(
        text = headerText,
        modifier = Modifier
            .padding(
                top = 12.dp,
                start = 12.dp
            ),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
}