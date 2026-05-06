package com.drcmind.kelasisuite.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Breakpoints {
    val Compact = 600.dp
    val Medium = 840.dp
    val Expanded = 1200.dp
    val Large = 1600.dp
}

object AdaptiveUtil {
    @Composable
    fun isCompact(width: Dp) = width < Breakpoints.Compact

    @Composable
    fun isMedium(width: Dp) = width >= Breakpoints.Compact && width < Breakpoints.Medium

    @Composable
    fun isExpanded(width: Dp) = width >= Breakpoints.Medium && width < Breakpoints.Expanded

    @Composable
    fun isLarge(width: Dp) = width >= Breakpoints.Expanded && width < Breakpoints.Large

    @Composable
    fun isExtraLarge(width: Dp) = width >= Breakpoints.Large

    @Composable
    fun isWide(width: Dp) = width >= Breakpoints.Medium
}