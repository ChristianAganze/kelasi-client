package com.drcmind.kelasisuite.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Streetview
import androidx.compose.material.icons.filled.Subject
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


sealed interface NavigationBarRoute : Route {
    val icon: ImageVector
    val label: String
}


@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Auth : Route {
        @Serializable
        data object Login : Route
        @Serializable
        data object ContactUs : Route
    }

    @Serializable
    data object SystemAdmin : Route {
        @Serializable
        data object Dashboard : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Dashboard
            override val label: String = "Tableau de bord"
        }

        @Serializable
        data object Curriculum : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.AccountTree
            override val label: String = "Curriculum"
        }

        @Serializable
        data object Subjects : NavigationBarRoute {
            override val icon: ImageVector = Icons.Filled.Book
            override val label: String = "Subject"
        }

        @Serializable
        data object Schools : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Apartment
            override val label: String = "Schools"
        }


        @Serializable
        data object Templates : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Architecture
            override val label: String = "Templates"
        }

        @Serializable
        data object Settings : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Settings
            override val label: String = "Settings"
        }

        val items = listOf(Dashboard,Curriculum, Subjects, Schools, Templates,  Settings)

        val stateSaver = Saver<NavigationBarRoute, String>(
            save = { it::class.qualifiedName ?: "" },
            restore = { qualifiedClass ->
                items.firstOrNull { it::class.qualifiedName == qualifiedClass } ?: Dashboard
            }
        )
    }

    @Serializable
    data object SchoolAdmin : Route {
        @Serializable
        data object Admin : Route {
            @Serializable
            data object Home : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Home
                override val label: String = "Accueil"

            }

            @Serializable
            data object Profile : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Person
                override val label: String = "Profile"
            }

            @Serializable
            data object Settings : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Settings
                override val label: String = "Settings"
            }
        }

        @Serializable
        data object Teacher : Route {
            @Serializable
            data object Project : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Home
                override val label: String = "Project"

                @Serializable
                data object ProjectList : Route

                @Serializable
                data class ProjectDetail(val id: Int) : Route

            }

            @Serializable
            data object Profile : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Person
                override val label: String = "Profile"
            }

            @Serializable
            data object Settings : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Settings
                override val label: String = "Settings"
            }
        }

        @Serializable
        data object SystemParent : Route {
            @Serializable
            data object Project : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Home
                override val label: String = "Project"

                @Serializable
                data object ProjectList : Route

                @Serializable
                data class ProjectDetail(val id: Int) : Route

            }

            @Serializable
            data object Profile : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Person
                override val label: String = "Profile"
            }

            @Serializable
            data object Settings : NavigationBarRoute {
                override val icon: ImageVector = Icons.Default.Settings
                override val label: String = "Settings"
            }
        }

    }


}