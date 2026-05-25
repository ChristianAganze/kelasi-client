package com.drcmind.kelasisuite.ui.schooladmin.pedagogy

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
fun PedagogyScreen(){

    val coroutineScope = rememberCoroutineScope()

    val pedagogyBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Pedagogy.Scheduling::class,
                        Route.SchoolAdmin.Pedagogy.Scheduling.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.ProgramRadar::class,
                        Route.SchoolAdmin.Pedagogy.ProgramRadar.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments::class,
                        Route.SchoolAdmin.Pedagogy.TeacherAssignments.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.Preparation::class,
                        Route.SchoolAdmin.Pedagogy.Preparation.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.ClassLog::class,
                        Route.SchoolAdmin.Pedagogy.ClassLog.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Pedagogy.Inspections::class,
                        Route.SchoolAdmin.Pedagogy.Inspections.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Pedagogy.Scheduling
    )

    var selectedDestination by rememberSaveable {
        mutableStateOf(Route.SchoolAdmin.Pedagogy.TabDestination.SCHEDULING.ordinal)
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
        Row{
            if(showPreviousButton){
                IconButton(onClick = {
                    coroutineScope.launch {
                        val previousPosition =
                            (scrollState.value - 300)
                                .coerceAtLeast(0)

                        scrollState.animateScrollTo(previousPosition)
                    }

                }){
                    Icon(imageVector = Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = null)
                }
            }
            PrimaryScrollableTabRow(
                scrollState = scrollState,
                selectedTabIndex = selectedDestination,
                modifier = Modifier.weight(1f),
            ) {
                Route.SchoolAdmin.Pedagogy.TabDestination.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = selectedDestination == index,
                        onClick = {
                            pedagogyBackStack.add(destination.route)
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
            if(showNextButton){
                IconButton(onClick = {
                    coroutineScope.launch {
                        val nextPosition =
                            (scrollState.value + 300)
                                .coerceAtMost(scrollState.maxValue)

                        scrollState.animateScrollTo(nextPosition)
                    }

                }){
                    Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
                }
            }

        }

        PedagogyNavigation(pedagogyBackStack = pedagogyBackStack)
    }
}