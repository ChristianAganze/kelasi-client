package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.navigation.Route
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun SchoolStructureScreen(
){
    val schoolStructureBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        Route.SchoolAdmin.Academics.SchoolStructure.Structure::class,
                        Route.SchoolAdmin.Academics.SchoolStructure.Structure.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.SchoolStructure.AddClass::class,
                        Route.SchoolAdmin.Academics.SchoolStructure.AddClass.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail::class,
                        Route.SchoolAdmin.Academics.SchoolStructure.ClassDetail.serializer()
                    )
                    subclass(
                        Route.SchoolAdmin.Academics.SchoolStructure.Structure::class,
                        Route.SchoolAdmin.Academics.SchoolStructure.Structure.serializer()
                    )

                }
            }
        },
        Route.SchoolAdmin.Academics.SchoolStructure.Structure
    )

    SchoolStructureNavigation(schoolStructureBackStack = schoolStructureBackStack)

}

