package com.drcmind.kelasisuite.ui.teacheradmin.classlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun TeacherClassLogScreen() {
    val coroutineScope = rememberCoroutineScope()

    val classLogBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.TeacherAdmin.ClassLog.NewEntry::class,
                        Route.TeacherAdmin.ClassLog.NewEntry.serializer()
                    )
                    subclass(
                        Route.TeacherAdmin.ClassLog.History::class,
                        Route.TeacherAdmin.ClassLog.History.serializer()
                    )
                }
            }
        },
        Route.TeacherAdmin.ClassLog.NewEntry
    )

    var selectedDestination by rememberSaveable {
        mutableStateOf(Route.TeacherAdmin.ClassLog.TabDestination.NEW_ENTRY.ordinal)
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val scrollState = rememberScrollState()
        val showNextButton by remember {
            derivedStateOf {
                scrollState.value < scrollState.maxValue
            }
        }
        val showPreviousButton by remember {
            derivedStateOf {
                scrollState.value > 0
            }
        }

        Row {
            if (showPreviousButton) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        val previousPosition = (scrollState.value - 300).coerceAtLeast(0)
                        scrollState.animateScrollTo(previousPosition)
                    }
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Précédent")
                }
            }

            PrimaryScrollableTabRow(
                scrollState = scrollState,
                selectedTabIndex = selectedDestination,
                modifier = Modifier.weight(1f),
            ) {
                Route.TeacherAdmin.ClassLog.TabDestination.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = selectedDestination == index,
                        onClick = {
                            classLogBackStack.add(destination.route)
                            selectedDestination = index
                        },
                        text = {
                            Text(
                                text = destination.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            if (showNextButton) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        val nextPosition = (scrollState.value + 300).coerceAtMost(scrollState.maxValue)
                        scrollState.animateScrollTo(nextPosition)
                    }
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Suivant")
                }
            }
        }

        TeacherClassLogNavigation(classLogBackStack = classLogBackStack)
    }
}
