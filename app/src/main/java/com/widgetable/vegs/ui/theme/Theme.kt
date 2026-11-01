package com.widgetable.vegs.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PinkPrimary = Color(0xFFFF7EB6)
val PinkDark = Color(0xFFE05A9A)
val PurpleSecondary = Color(0xFFA78BFA)
val SoftBackground = Color(0xFFFFF5F9)
val SoftSurface = Color(0xFFFFFFFF)
val AccentCyan = Color(0xFF67E8F9)
val PetGreen = Color(0xFF86EFAC)
val HappyYellow = Color(0xFFFDE047)
val LovePink = Color(0xFFF9A8D4)

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD6E8),
    onPrimaryContainer = Color(0xFF3D0020),
    secondary = PurpleSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF2E1065),
    tertiary = AccentCyan,
    background = SoftBackground,
    onBackground = Color(0xFF1F1A1D),
    surface = SoftSurface,
    onSurface = Color(0xFF1F1A1D),
    surfaceVariant = Color(0xFFFCE7F3),
    onSurfaceVariant = Color(0xFF514347)
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = Color(0xFF3D0020),
    primaryContainer = Color(0xFF7A2E55),
    onPrimaryContainer = Color(0xFFFFD6E8),
    secondary = PurpleSecondary,
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = AccentCyan,
    background = Color(0xFF1A1216),
    onBackground = Color(0xFFFCE7F3),
    surface = Color(0xFF251C21),
    onSurface = Color(0xFFFCE7F3),
    surfaceVariant = Color(0xFF514347),
    onSurfaceVariant = Color(0xFFD6C2C9)
)

@Composable
fun WidgetableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
