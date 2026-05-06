package com.drcmind.kelasisuite.ui.components

import androidx.compose.ui.graphics.Color


object AppColors {

    val primary = Color(0xFF000000)

    val onPrimary = Color(0xFFFFFFFF)

    val surfaceContent = Color(0xFF111827)

    val surfaceBackground = Color(0xFFF3F4F6)

    val surfaceInput = Color(0xFFF9FAFB)

    val surfaceBorder = Color(0xFFE5E7EB)

    val surfaceContainer = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF1F3FF)
    val surfaceContainerHigh = Color(0xFFE1E8FD)
    val onSurfaceVariant = Color(0xFF4C4546)
    val outlineVariant = Color(0xFFCFC4C5)
    val secondaryContainer = Color(0xFFDCE2F3)

    val textPrimary = Color(0xFF1F2937)

    val textLabel = Color(0xFF4A4A4A)

    val textSecondary = Color(0xFF6B7280)

    val textTertiary = Color(0xFF374151)

    val textDisabled = Color(0xFF4B5563)

    val textPlaceholder = Color(0xFFD1D5DB)

    val textIcon = Color(0xFF9CA3AF)

    val disabled = Color(0xFF9CA3AF)

    val error = Color(0xFFBA1A1A)

    val errorBackground = Color(0xFFFFEBEE)

    val socialGoogle = Color(0xFF4285F4)

    val socialFacebook = Color(0xFF1877F2)

    val socialGithub = Color(0xFF000000)

    val sucess = Color(0xFF0E6245)

    object Button {
        val primary = AppColors.primary
        val primaryText = AppColors.onPrimary
        val disabled = AppColors.disabled
        val border = AppColors.surfaceBorder
    }

    object Input {
        val background = AppColors.surfaceInput
        val border = AppColors.surfaceBorder
        val errorBorder = AppColors.error
        val text = AppColors.textPrimary
        val placeholder = AppColors.textPlaceholder
        val icon = AppColors.textIcon
        val label = AppColors.textLabel
    }

    object Text {
        val primary = AppColors.textPrimary
        val secondary = AppColors.textSecondary
        val tertiary = AppColors.textTertiary
        val disabled = AppColors.textDisabled
        val label = AppColors.textLabel
        val error = AppColors.error
    }

    object Surface {
        val background = AppColors.surfaceBackground
        val container = AppColors.surfaceContainer
        val input = AppColors.surfaceInput
        val border = AppColors.surfaceBorder
    }

    object Social {
        val google = AppColors.socialGoogle
        val facebook = AppColors.socialFacebook
        val github = AppColors.socialGithub
    }
}