package com.drcmind.kelasisuite.ui.schooladmin.staff_hr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun StaffHrScreen() {
    val staffHrBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.StaffHR.Teachers::class,
                        Route.SchoolAdmin.StaffHR.Teachers.serializer()
                    )
                }
            }
        },
        Route.SchoolAdmin.StaffHR.Teachers
    )

    var selectedDestination by rememberSaveable {
        mutableStateOf(Route.SchoolAdmin.StaffHR.TabDestination.TEACHERS.ordinal)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryScrollableTabRow(selectedTabIndex = selectedDestination, containerColor = Color.Transparent) {
            Route.SchoolAdmin.StaffHR.TabDestination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        staffHrBackStack.add(destination.route)
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
        StaffHrNavigation(staffHrBackStack = staffHrBackStack)
    }
}