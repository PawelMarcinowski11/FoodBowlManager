package marcinowski.pawel.foodmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Cactus,

    secondary = Ash,
    secondaryVariant = Cactus,

    background = CharredAsh,
    surface = CharredAsh,

    onPrimary = PaleCactus,
    onSecondary = PaleCactus,
    onBackground = WhiteAsh,
    onSurface = WhiteAsh,
)

private val LightColorPalette = lightColors(
    primary = Cactus,

    secondary = Dough,
    secondaryVariant = Cactus,

    background = PaleDough,
    surface = PaleDough,

    onPrimary = PaleCactus,
    onSecondary = PaleCactus,
    onBackground = CharredDough,
    onSurface = CharredDough,
)

@Composable
fun FoodManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
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