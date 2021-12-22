package marcinowski.pawel.foodmanager.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import marcinowski.pawel.foodmanager.R
import marcinowski.pawel.foodmanager.dataStore
import marcinowski.pawel.foodmanager.foodNotifications

@Composable
fun SettingsScreen(darkTheme: MutableState<Boolean>) {
    Column (
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
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
            ThemeSettings(darkTheme)
            Divider(thickness = 1.dp)
        }
    }
}

@Composable
private fun NotificationsSettings() {
    val context = LocalContext.current
    val useNotifications = remember { context.dataStore.data
        .map { settings ->
            settings[booleanPreferencesKey("useNotifications")] ?: true
        }}.collectAsState(true)

    SectionHeader(stringResource(R.string.section_notifications))
    NotificationsSwitch(useNotifications)
    Divider(thickness = 1.dp)
    SectionHeader(stringResource(R.string.section_notification_types))
    DailyNotificationsSwitch(useNotifications)
    ShortDateNotificationsSwitch(useNotifications)
}

@Composable
private fun ThemeSettings(darkTheme: MutableState<Boolean>) {
    val context = LocalContext.current
    val useSystemTheme = remember { context.dataStore.data
        .map { settings ->
            settings[booleanPreferencesKey("useSystemTheme")] ?: true
        }}.collectAsState(true)

    SectionHeader(stringResource(R.string.section_themes))
    SystemThemeSwitch(useSystemTheme, darkTheme)
    ApplicationThemeSwitch(useSystemTheme, darkTheme)
}

@Composable
private fun NotificationsSwitch(useNotifications: State<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_notifications),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
                .fillMaxWidth(0.7f)
        )
        Switch(
            checked = useNotifications.value,
            onCheckedChange = { checked ->
                coroutineScope.launch {
                    context.dataStore.edit { settings ->
                        settings[booleanPreferencesKey("useNotifications")] = checked
                        if (checked)
                            foodNotifications().setNotifications(context, 7)
                        else
                            foodNotifications().cancelNotifications(context)
                    }
                }
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
private fun ShortDateNotificationsSwitch(useNotifications: State<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val notifyOnShortDate = remember { context.dataStore.data
        .map { settings ->
            settings[booleanPreferencesKey("notifyOnShortDate")] ?: false
        }}.collectAsState(false)
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_short_date_notifications),
            color = if (useNotifications.value) MaterialTheme.colors.onSurface
            else MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
                .fillMaxWidth(0.7f)
        )
        Switch(
            enabled = useNotifications.value,
            checked = notifyOnShortDate.value,
            onCheckedChange = { checked ->
                coroutineScope.launch {
                    context.dataStore.edit { settings ->
                        settings[booleanPreferencesKey("notifyOnShortDate")] = checked
                        if (checked)
                            settings[booleanPreferencesKey("notifyDaily")] = false
                    }
                }
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
private fun DailyNotificationsSwitch(useNotifications: State<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val notifyDaily = remember { context.dataStore.data
        .map { settings ->
            settings[booleanPreferencesKey("notifyDaily")] ?: true
        }}.collectAsState(true)
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_daily_notifications),
            color = if (useNotifications.value) MaterialTheme.colors.onSurface
            else MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
                .fillMaxWidth(0.7f)
        )
        Switch(
            enabled = useNotifications.value,
            checked = notifyDaily.value,
            onCheckedChange = { checked ->
                coroutineScope.launch {
                    context.dataStore.edit { settings ->
                        settings[booleanPreferencesKey("notifyDaily")] = checked
                        if (checked)
                            settings[booleanPreferencesKey("notifyOnShortDate")] = false
                    }
                }
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
private fun SystemThemeSwitch(
    useSystemTheme: State<Boolean>,
    darkTheme: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_use_system_theme),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
                .fillMaxWidth(0.7f)
        )
        Switch(
            checked = useSystemTheme.value,
            onCheckedChange = { checked ->
                coroutineScope.launch {
                    context.dataStore.edit { settings ->
                        settings[booleanPreferencesKey("useSystemTheme")] = checked
                    }
                }
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 10.dp)
        )
        if (useSystemTheme.value) {
            darkTheme.value = isSystemInDarkTheme()
        }
    }
}

@Composable
private fun ApplicationThemeSwitch(
    useSystemTheme: State<Boolean>,
    darkTheme: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val useDarkTheme = remember { context.dataStore.data
        .map { settings ->
            settings[booleanPreferencesKey("useDarkTheme")] ?: true
        }}.collectAsState(true)

    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.switch_use_dark_theme),
            color = if (!useSystemTheme.value) MaterialTheme.colors.onSurface
                    else MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 2.dp, start = 10.dp)
                .fillMaxWidth(0.7f)
        )
        Switch(
            enabled = !useSystemTheme.value,
            checked = useDarkTheme.value,
            onCheckedChange = { checked ->
                coroutineScope.launch {
                    context.dataStore.edit { settings ->
                        settings[booleanPreferencesKey("useDarkTheme")] = checked
                    }
                }
            },
            modifier = Modifier
                .height(40.dp)
                .scale(1.0f)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp, end = 10.dp)
        )
        if (!useSystemTheme.value) {
            darkTheme.value = useDarkTheme.value
        }
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