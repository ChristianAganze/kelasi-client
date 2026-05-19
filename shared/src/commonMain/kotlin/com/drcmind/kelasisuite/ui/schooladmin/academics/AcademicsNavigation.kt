package com.drcmind.kelasisuite.ui.schooladmin.academics


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.drcmind.kelasisuite.navigation.Route
import com.drcmind.kelasisuite.ui.schooladmin.academics.calendar_periods.CalendarPeriodsScreen
import com.drcmind.kelasisuite.ui.schooladmin.academics.school_structure.SchoolStructureScreen

@Composable
fun AcademicsNavigation(
    modifier: Modifier = Modifier,
    academicsBackStack: NavBackStack<NavKey>
) {
    NavDisplay(
        modifier = modifier,
        backStack = academicsBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.SchoolAdmin.Academics.CalendarPeriod> {
                CalendarPeriodsScreen()
            }
            entry<Route.SchoolAdmin.Academics.SchoolStructure> {
                SchoolStructureScreen()
            }

            entry<Route.SchoolAdmin.Academics.Enrollment> {
                CourseAffectation()
            }

            entry<Route.SchoolAdmin.Academics.EvaluationGrading> {
                Column {
                    Text("Barèmes (Weightings) : Définir le maximum des points pour chaque cours et chaque période.")
                    Text("Grilles de Cotes : Saisir ou consulter les notes brutes envoyées par les enseignants.")
                }
            }

            entry<Route.SchoolAdmin.Academics.DeliberationsConduct> {
                Column {
                    Text("Saisie de la Conduite par les titulaires. ")
                    Text("Calcul automatique du pourcentage d'Application.")
                }
            }

            entry<Route.SchoolAdmin.Academics.ReportCards> {
                Column {
                    Text("Génération, visualisation et impression en masse des bulletins officiels PDF.")
                }
            }
        }
    )
}