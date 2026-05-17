package com.drcmind.kelasisuite.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import kelasisuite.shared.generated.resources.Res
import kelasisuite.shared.generated.resources.lexend_black
import kelasisuite.shared.generated.resources.lexend_bold
import kelasisuite.shared.generated.resources.lexend_extrabold
import kelasisuite.shared.generated.resources.lexend_extralight
import kelasisuite.shared.generated.resources.lexend_light
import kelasisuite.shared.generated.resources.lexend_medium
import kelasisuite.shared.generated.resources.lexend_regular
import kelasisuite.shared.generated.resources.lexend_semibold
import kelasisuite.shared.generated.resources.lexend_thin
import org.jetbrains.compose.resources.Font

object TypographyScale {
    const val COMPACT = 0.62f
    const val LARGE = 0.7f
}

fun scaledSp(size: Int, scale: Float) = (size * scale).sp

@Composable
fun lexendFontFamily() = FontFamily(

    Font(
        Res.font.lexend_thin,
        weight = FontWeight.Thin,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_extralight,
        weight = FontWeight.ExtraLight,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_light,
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_medium,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_semibold,
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_bold,
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_extrabold,
        weight = FontWeight.ExtraBold,
        style = FontStyle.Normal
    ),

    Font(
        Res.font.lexend_black,
        weight = FontWeight.Black,
        style = FontStyle.Normal
    )
)

@Composable
fun appTypography(
    fontFamily: FontFamily,
    scale: Float = TypographyScale.COMPACT
): Typography {

    return Typography(

        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(48, scale),
            fontWeight = FontWeight.Bold
        ),

        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(40, scale),
            fontWeight = FontWeight.Bold
        ),

        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(32, scale),
            fontWeight = FontWeight.SemiBold
        ),

        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(26, scale),
            fontWeight = FontWeight.SemiBold
        ),

        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(22, scale),
            fontWeight = FontWeight.SemiBold
        ),

        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(20, scale),
            fontWeight = FontWeight.SemiBold
        ),

        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(18, scale),
            fontWeight = FontWeight.Medium
        ),

        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(15, scale),
            fontWeight = FontWeight.Medium
        ),

        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(13, scale),
            fontWeight = FontWeight.Medium
        ),

        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(15, scale),
            fontWeight = FontWeight.Normal
        ),

        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(14, scale),
            fontWeight = FontWeight.Normal
        ),

        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(12, scale),
            fontWeight = FontWeight.Normal
        ),

        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(14, scale),
            fontWeight = FontWeight.Medium
        ),

        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(12, scale),
            fontWeight = FontWeight.Medium
        ),

        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = scaledSp(10, scale),
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
fun rememberTypography(
    fontFamily: FontFamily,
    windowSizeClass: WindowSizeClass
): Typography {

    val scale = when {

        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        ) -> TypographyScale.LARGE

        else -> TypographyScale.COMPACT
    }

    return appTypography(fontFamily, scale)
}
