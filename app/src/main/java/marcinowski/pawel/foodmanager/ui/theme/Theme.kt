package marcinowski.pawel.foodmanager.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color.LightGray,
    primaryVariant = Color.Black,
    secondary = Color.DarkGray
)


val Cactus = Color(0xFF68996E)
val PaleCactus = Color(0xFFEAFBAA)
val CharredDough = Color(0xFF2C1106)
val Dough = Color(0xFF6C4126)
val PaleDough = Color(0xFFFEFDFA)


@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Cactus, //Button, NavBar, kursor w TextForm
    primaryVariant = Color.White, //nw
    secondary = Dough, //RadioBox
    secondaryVariant = Cactus, //Switch on

    background = PaleDough, //kolor tła
    surface = PaleDough, //Card ma ten kolor

    onPrimary = PaleCactus, //Button click, Button text
    onSecondary = Color.White, //???
    onBackground = CharredDough, //napisy
    onSurface = Dough, //ramka przy TextForm, Switch off

    /* Other default colors to override
    */
)

@Composable
fun FoodManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}