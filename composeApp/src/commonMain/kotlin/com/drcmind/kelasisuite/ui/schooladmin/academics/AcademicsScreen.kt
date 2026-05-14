package com.drcmind.kelasisuite.ui.schooladmin.academics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun AcademicsScreen() {

    val academicsBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Academics.CalendarPeriod::class,
                        Route.SchoolAdmin.Academics.CalendarPeriod.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.SchoolStructure::class,
                        Route.SchoolAdmin.Academics.SchoolStructure.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.Assignment::class,
                        Route.SchoolAdmin.Academics.Assignment.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.EvaluationGrading::class,
                        Route.SchoolAdmin.Academics.EvaluationGrading.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.DeliberationsConduct::class,
                        Route.SchoolAdmin.Academics.DeliberationsConduct.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.ReportCards::class,
                        Route.SchoolAdmin.Academics.ReportCards.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.Academics.CalendarPeriod
    )

    var selectedDestination by rememberSaveable {
        mutableStateOf(Route.SchoolAdmin.Academics.TabDestination.CALENDAR_PERIOD.ordinal)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedDestination) {
            Route.SchoolAdmin.Academics.TabDestination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        academicsBackStack.add(destination.route)
                        selectedDestination = index
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        AcademicsNavigation(academicsBackStack = academicsBackStack)
    }

}
