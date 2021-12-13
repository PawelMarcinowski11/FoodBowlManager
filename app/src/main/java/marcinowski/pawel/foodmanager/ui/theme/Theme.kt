package marcinowski.pawel.foodmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val Cactus = Color(0xFF68996E)
val PaleCactus = Color(0xFFEAFBAA)
val CharredDough = Color(0xFF2C1106)
val Dough = Color(0xFF5C4126)
val PaleDough = Color(0xFFFEFDFA)
val CharredAsh = Color (0xFF090606)
val Ash = Color (0xFF8E8383)
val WhiteAsh = Color (0xFFD8D3D3)

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