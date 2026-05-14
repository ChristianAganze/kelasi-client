package com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.drcmind.kelasisuite.domain.dto.SchoolClassDTO
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.components.AppIcons
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel

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

