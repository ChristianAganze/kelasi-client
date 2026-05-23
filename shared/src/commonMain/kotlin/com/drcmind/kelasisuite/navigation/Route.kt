package com.drcmind.kelasisuite.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.WorkspacePremium
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
    data object Loading : Route

    @Serializable
    data object Auth : Route

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

        val items = listOf(Dashboard, Curriculum, Subjects, Schools, Templates, Settings)

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
        data object SchoolDashboard : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Dashboard
            override val label: String = "Tableau de bord"
        }

        @Serializable
        data object Academics : NavigationBarRoute {
            override val icon: ImageVector = Icons.AutoMirrored.Filled.LibraryBooks
            override val label: String = "Affaires scolaires"

            @Serializable
            data object CalendarPeriod : Route {
                @Serializable
                data object Main : Route

                @Serializable
                data object Supporting : Route
            }

            @Serializable
            data object SchoolStructure : Route {
                @Serializable
                data object Structure : Route

                @Serializable
                data class AddClass(val classId: Long? = null) : Route

                @Serializable
                data class ClassDetail(val classId: Long, val className: String) : Route {
                    @Serializable
                    data object Main : Route

                    @Serializable
                    data object Supporting : Route
                }
            }

            @Serializable
            data object Assignment : Route
            @Serializable
            data object StudentEnrollment : Route{
                @Serializable
                data object Enrollment : Route{
                    @Serializable data object List : Route
                    @Serializable data class Profile(val studentId: Long?) : Route
                }
                @Serializable
                data object Students : Route{
                    @Serializable data object List : Route
                    @Serializable data class Profile(val studentId: Long) : Route
                }

                enum class TabDestination(
                    val route: Route,
                    val label: String,
                    val icon: ImageVector,
                    val contentDescription: String
                ) {
                    ENROLLMENT(Enrollment, "Inscriptions", Icons.AutoMirrored.Filled.Assignment, "Enrollment"),
                    STUDENTS(Students, "Elèves", Icons.Default.School, "Students"),
                }

            }
            @Serializable
            data object EvaluationGrading : Route

            @Serializable
            data object DeliberationsConduct : Route

            @Serializable
            data object ReportCards : Route

            enum class TabDestination(
                val route: Route,
                val label: String,
                val icon: ImageVector,
                val contentDescription: String
            ) {
                CALENDAR_PERIOD(CalendarPeriod, "Calendrier et périodes", Icons.Default.CalendarMonth, "calendar"),
                SCHOOL_STRUCTURE(SchoolStructure, "Structure de l'école", Icons.Default.AccountTree, "AccountTree"),
                STUDENT_ENROLLMENT(StudentEnrollment, "Suivi Scolaire", Icons.AutoMirrored.Filled.Assignment, "Enrollment"),
                EVALUATION_GRADING(EvaluationGrading, "Evaluation et cotes", Icons.Default.Numbers, "Evaluation"),
                DELIBERATION_CONDUCT(DeliberationsConduct, "Délibérations & conduite", Icons.Default.ControlPointDuplicate, "Deliberation et conduite"),
                REPORT_CARDS(ReportCards, "Bulletins", Icons.Default.Report, "Report")
            }
        }

        @Serializable
        data object Pedagogy : NavigationBarRoute {
            override val icon: ImageVector = Icons.AutoMirrored.Filled.Accessible
            override val label: String = "Pédagogie"

            @Serializable data object Scheduling : Route
            @Serializable data object ProgramRadar : Route
            @Serializable data object Assignment : Route
            @Serializable data object Preparation : Route
            @Serializable data object ClassLog : Route
            @Serializable data object Inspections : Route

            enum class TabDestination(
                val route: Route,
                val label: String,
                val icon: ImageVector,
                val contentDescription: String
            ) {
                SCHEDULING(Scheduling, "Horaires des cours", Icons.Default.CalendarMonth, "calendar"),
                PROGRAM_RADAR(ProgramRadar, "Radar Anti-Retard", Icons.Default.Check, "AccountTree"),
                ASSIGNMENT(Pedagogy.Assignment, "Affectations & Titulariat", Icons.AutoMirrored.Filled.Assignment, "Assignment"),
                PREPARATION(Preparation, "Préparations", Icons.Default.WorkspacePremium, "Evaluation"),
                CLASSLOG(ClassLog, "Journaux de Classe",
                    Icons.AutoMirrored.Filled.Notes, "Deliberation et conduite"),
                INSPECTIONS(Inspections, "Inspections", Icons.Default.Analytics, "Report")
            }
        }

        @Serializable
        data class AddStudent(val studentId: Long? = null) : Route

        @Serializable
        data class AddTeacher(val teacherId: Long? = null) : Route

        @Serializable
        data class StudentDetail(val studentId: Long) : Route

        @Serializable
        data class TeacherDetail(val teacherId: Long) : Route


        @Serializable
        data object Profile : Route

        @Serializable
        data object Parents : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.PeopleAlt
            override val label: String = "Parents"

            @Serializable data object List : Route
            @Serializable data class Profile(val parentId : Long) : Route
        }

        @Serializable
        data object StaffHR : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.GroupWork
            override val label: String = "Staffs & HR"

            @Serializable
            data object Teachers : Route {
                @Serializable
                data object ListDetails : Route {
                    @Serializable
                    data object List : Route

                    @Serializable
                    data class Profile(val teacherId: Long) : Route
                }

                @Serializable
                data class ProfileDetails(val teacherId: Long) : Route

                @Serializable
                data class AddUpdate(val teacherId: Long?) : Route
            }

            enum class TabDestination(
                val route: Route,
                val label: String,
                val icon: ImageVector,
                val contentDescription: String
            ) {
                TEACHERS(Teachers, "Enseignants", Icons.Default.PeopleAlt, "Teachers"),
            }
        }

        @Serializable
        data object Finance : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Money
            override val label: String = "Finance & Comptabilité"
        }

        @Serializable
        data object Logistics : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Warehouse
            override val label: String = "Logistique & opérations"
        }

        @Serializable
        data object Communication : NavigationBarRoute {
            override val icon: ImageVector = Icons.AutoMirrored.Filled.Chat
            override val label: String = "Communication"
        }

        @Serializable
        data object Settings : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Settings
            override val label: String = "Paramètres"
        }

        val items = listOf(
            SchoolDashboard,
            Academics,
            Pedagogy,
            Parents,
            StaffHR,
            Finance,
            Logistics,
            Communication,
            Settings,
        )

        val stateSaver = Saver<NavigationBarRoute, String>(
            save = { it::class.qualifiedName ?: "" },
            restore = { qualifiedClass ->
                items.firstOrNull { it::class.qualifiedName == qualifiedClass } ?: SchoolDashboard
            }
        )

    }

    @Serializable
    data object TeacherAdmin : Route {
        @Serializable
        data object Project : NavigationBarRoute {
            override val icon: ImageVector = Icons.Default.Home
            override val label: String = "Project"
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
    data object ParentAdmin : Route {
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